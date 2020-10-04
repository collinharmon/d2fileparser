/*******************************************************************************
 *
 * Copyright 2007 Randall
 *
 * This file is part of gomule.
 *
 * gomule is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * gomule is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * gomlue; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 *
 ******************************************************************************/
package stash;

import item.*;
import util.*;

import java.io.*;
import java.util.*;

/**
 * @author Marco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class D2Stash
{
    //    private String		iFileName;
    private ArrayList	iItems;

    private D2BitReader	iBR;
    private boolean		iHC;
    private boolean		iSC;

    private int			iCharLvl = 75; // default char lvl for properties

    private String iFileName;

    private File lFile;

//    private int iItemlistStart;
//    private int iItemlistEnd;

    public D2Stash(String pFileName) throws Exception
    {
        iFileName = pFileName;
        if ( iFileName == null || !iFileName.toLowerCase().endsWith(".d2x") )
        {
            throw new Exception("Incorrect Stash file name");
        }
        iItems = new ArrayList();

        lFile = new File(iFileName);

        iSC = lFile.getName().toLowerCase().startsWith("sc_");
        iHC = lFile.getName().toLowerCase().startsWith("hc_");

        if ( !iSC && !iHC )
        {
            iSC = true;
            iHC = true;
        }

        iBR = new D2BitReader(iFileName);

        if ( !iBR.isNewFile() )
        {
            iBR.set_byte_pos(0);
            byte lBytes[] = iBR.get_bytes(3);
            String lStart = new String(lBytes);
            if ( "D2X".equals(lStart) )
            {
                readAtmaItems();
            }
            // clear status
        }
    }

    public String getFilename()
    {
        return iFileName;
    }

    public boolean isHC()
    {
        return iHC;
    }

    public boolean isSC()
    {
        return iSC;
    }

    public ArrayList getItemList()
    {
        return iItems;
    }

    public void addItem(D2Item pItem)
    {
        if ( pItem != null )
        {
            iItems.add(pItem);
            pItem.setCharLvl(iCharLvl);
        }
    }

    public boolean containsItem(D2Item pItem)
    {
        return iItems.contains(pItem);
    }

    public void removeItem(D2Item pItem)
    {
        iItems.remove(pItem);
    }

    public ArrayList removeAllItems()
    {
        ArrayList lReturn = new ArrayList();
        lReturn.addAll( iItems );

        iItems.clear();

        return lReturn;
    }


    public int getNrItems()
    {
        return iItems.size();
    }

    private void readAtmaItems() throws Exception
    {

        iBR.set_byte_pos( 7 );
        long lOriginal = iBR.read( 32 );

        long lCalculated = calculateAtmaCheckSum();

        if ( lOriginal == lCalculated )
        {
            iBR.set_byte_pos(3);

            long lNumItems = iBR.read(16);

            long lVersionNr = iBR.read(16);

            if ( lVersionNr == 96 )
            {
                readItems(lNumItems);
            }else{
                throw new Exception("Stash Version Incorrect!");
            }
        }
    }

    private long calculateAtmaCheckSum()
    {
        long lCheckSum;
        lCheckSum = 0;


        iBR.set_byte_pos( 0 );
        // calculate a new checksum
        for ( int i = 0 ; i < iBR.get_length() ; i++ )
        {
            long lByte = iBR.read( 8 );
            if ( i >= 7 && i <= 10 )
            {
                lByte = 0;
            }

            long upshift = lCheckSum << 33 >>> 32;
            long add = lByte + ( ( lCheckSum >>> 31 ) == 1 ? 1 : 0 );
            lCheckSum = upshift + add;
        }

//		System.err.println("Test " + lOriginal + " - " + lCheckSum + " = " + (lOriginal == lCheckSum) );
        return lCheckSum;
    }

    private void readItems(long pNumItems) throws Exception
    {
        int lLastItemEnd = 11;

        for ( int i = 0 ; i < pNumItems ; i++ )
        {
            int lItemStart = iBR.findNextFlag("JM", lLastItemEnd);

            D2Item lItem = new D2Item(iFileName, iBR, lItemStart, iCharLvl);
            lLastItemEnd = lItemStart + lItem.getItemLength();
            iItems.add(lItem);
        }
    }

    /*public void saveInternal(D2Project pProject)
    {
        // backup file
        D2Backup.backup(pProject, iFileName, iBR);

        int size = 0;
        for (int i = 0; i < iItems.size(); i++)
            size += ((D2Item) iItems.get(i)).get_bytes().length;
        byte[] newbytes = new byte[size+11];
        newbytes[0] = 'D';
        newbytes[1] = '2';
        newbytes[2] = 'X';
        int pos = 11;
        for (int i = 0; i < iItems.size(); i++)
        {
            byte[] item_bytes = ((D2Item) iItems.get(i)).get_bytes();
            for (int j = 0; j < item_bytes.length; j++)
                newbytes[pos++] = item_bytes[j];
        }

        iBR.setBytes(newbytes);

        iBR.set_byte_pos(3);
        iBR.write(iItems.size(), 16);
        iBR.write(96,16); // version 96
//        iBR.replace_bytes(11, iBR.get_length(), newbytes);

        long lCheckSum1 = calculateAtmaCheckSum();
//        System.err.println("CheckSum at saving: " + lCheckSum1 );

        iBR.set_byte_pos(7);
        iBR.write(lCheckSum1, 32);

        iBR.set_byte_pos(7);
        long lCheckSum2 = iBR.read(32);

//        long lCheckSum3 = calculateGoMuleCheckSum();
//        System.err.println("CheckSum after insert: " + lCheckSum3 );

        if ( lCheckSum1 == lCheckSum2 )
        {
            iBR.save();
            setModified(false);
        }
        else
        {
            System.err.println("Incorrect CheckSum");
        }
    }*/

    public void fullDump(PrintWriter pWriter)
    {
        pWriter.println( iFileName );
        pWriter.println();
        if ( iItems != null )
        {
            for ( int i = 0 ; i < iItems.size() ; i++ )
            {
                D2Item lItem = (D2Item) iItems.get(i);
                lItem.toWriter(pWriter);
            }
        }
        pWriter.println( "Finished: " + iFileName );
        pWriter.println();
    }

    public String getFileNameEnd(){
        return lFile.getName();
    }

}
