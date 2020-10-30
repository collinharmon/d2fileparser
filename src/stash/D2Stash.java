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
    private boolean		iIsSharedStash;
    private long numStashes = 1;  //one by default
    private int iSharedGold;

    private int			iCharLvl = 75; // default char lvl for properties

    private String iFileName;

    private File lFile;

//    private int iItemlistStart;
//    private int iItemlistEnd;

    public D2Stash(String pFileName) throws Exception
    {
        iFileName = pFileName;

        if ( iFileName != null && (iFileName.toLowerCase().endsWith(".d2x") || iFileName.toLowerCase().endsWith(".sss")))
        {
            iItems = new ArrayList();

            lFile = new File(iFileName);

            iBR = new D2BitReader(iFileName);

            if ( !iBR.isNewFile() )
            {
                iBR.set_byte_pos(0);
                if(iFileName.toLowerCase().endsWith(".d2x"))
                {
                    iBR.set_byte_pos(10);
                }
                else
                {
                    iIsSharedStash = true;
                    iBR.set_byte_pos(4);
                    byte lFileVersion[] = iBR.get_bytes(2);
                    if((Byte.toUnsignedInt(lFileVersion[1]) << 8 | Byte.toUnsignedInt(lFileVersion[0])) == 12592) //shared gold == 0
                    {
                        iSharedGold = 0;
                        iBR.set_byte_pos(6);  //numStashes at 0x6 when shared gold == 0
                    }
                    else
                    {
                        iBR.set_byte_pos(6);
                        byte lSharedGoldAmount[] = iBR.get_bytes(4);
                        iSharedGold = Byte.toUnsignedInt(lSharedGoldAmount[3]) << 24 | Byte.toUnsignedInt(lSharedGoldAmount[2]) << 16 |
                                Byte.toUnsignedInt(lSharedGoldAmount[1]) << 8 | Byte.toUnsignedInt(lSharedGoldAmount[0]);
                        iBR.set_byte_pos(10); //numStashes at 0xA when shared gold == 0
                    }
                }
                byte lBytes[] = iBR.get_bytes(4);

                numStashes = Byte.toUnsignedInt(lBytes[3]) << 24 | Byte.toUnsignedInt(lBytes[2]) << 16 | Byte.toUnsignedInt(lBytes[1]) << 8 | Byte.toUnsignedInt(lBytes[0]);

                if(numStashes > 0)
                {
                    readAtmaItems(numStashes);
                }


            }
        }
        else
        {
            throw new Exception("Incorrect Stash file name");
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

    public int getSharedGold()
    {
        return iSharedGold;
    }

    public long getNrStashes()
    {
        return numStashes;
    }

    public boolean isSharedStash()
    {
        return iIsSharedStash;
    }

    private void readAtmaItems(long numStashes) throws Exception
    {

        for(long i = 0; i < numStashes; i++)
        {
            int lNumItems = 0;
            int lCurrentItem = iBR.findNextFlag("JM", iBR.get_byte_pos());
            if (lCurrentItem == -1) continue;
            iBR.set_byte_pos(lCurrentItem + 2);
            byte lNumItemsBytes[] = iBR.get_bytes(2);
            lNumItems = lNumItemsBytes[1] << 8 | lNumItemsBytes[0];

            if(lNumItems > 0)
            {
                readItems(lNumItems, i+1);
            }
        }
    }

    /*private long calculateAtmaCheckSum()
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
    }*/

    private void readItems(long pNumItems, long pStashPage) throws Exception
    {

        for ( int i = 0 ; i < pNumItems ; i++ )
        {
            int lItemStart = iBR.findNextFlag("JM", iBR.get_byte_pos());

            D2Item lItem = new D2Item(iFileName, iBR, lItemStart, iCharLvl);
            lItem.setStashPage(pStashPage);
            iBR.set_byte_pos(lItemStart + lItem.getItemLength());
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
