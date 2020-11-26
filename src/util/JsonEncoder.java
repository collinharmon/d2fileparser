package util;
import character.D2Character;
import org.json.simple.JSONArray;
import stash.D2Stash;
import org.json.simple.JSONObject;
import item.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class JsonEncoder {

    private D2Character d2Character;
    private D2Stash d2Stash;

    public JsonEncoder(D2Stash stash){
        d2Stash = stash;
    }

    public JsonEncoder(D2Character d2Char){
        d2Character = d2Char;
    }

    public ArrayList generateJsonStash(){

        ArrayList<JSONObject> fullStash = new ArrayList<JSONObject>();

        JSONObject stashData = new JSONObject();

        stashData.put("numItems", d2Stash.getNrItems());
        stashData.put("isShared", d2Stash.isSharedStash());
        if (d2Stash.isSharedStash()){
            stashData.put("stashOwner", "shared");
        }
        else{
            //temp
            stashData.put("stashOwner", "player");
        }
        stashData.put("numStashes", d2Stash.getNrStashes());
        stashData.put("sharedGold", d2Stash.getSharedGold());
        stashData.put("filename", d2Stash.getFilename());

        fullStash.add(stashData);



        fullStash.addAll(getJSONItemList(d2Stash.getItemList()));

        return fullStash;
    }

    private ArrayList getJSONItemList(ArrayList items){

        ArrayList<JSONObject> jsonItemListObjs = new ArrayList<JSONObject>();
        //JSONArray jsonProps = new JSONArray();

        int totalNumItemsToProcess = items.size();
        int numItems;
        int jsonItemFiles = 1;
        if(totalNumItemsToProcess > 1000){
            jsonItemFiles = (int)Math.ceil(((double)totalNumItemsToProcess/1000));
            numItems = 1000;
        }
        else numItems = totalNumItemsToProcess;

        for(int numFiles = 0; numFiles < jsonItemFiles; numFiles++) {
            JSONArray jsonItems = new JSONArray();
            JSONObject arrayList = new JSONObject();
            int offset = numFiles * 1000;
            for (int i = 0; i < numItems; i++) {
                D2Item currItem = (D2Item) items.get(offset+i);
                JSONObject jsonItem = new JSONObject();

                /*Not interested in gathering prop/socketed data yet*/
                //jsonItem.put("Props", getJSONPropsList(currItem.getPropCollection()));
                //if(currItem.getSocketNrFilled() > 0)
                //jsonItem.put("SocketedItems", getJSONSocketedItemsList(currItem.getSocketedItems()) );
                //else
                //jsonItem.put("SocketedItems", null);

                JSONObject jsonItemData = getJSONItemData(currItem);
                jsonItem.putAll(jsonItemData);

                jsonItems.add(jsonItem);
            }
            arrayList.put("Items", jsonItems);
            jsonItemListObjs.add(arrayList);
            /*subtract number of items just processed*/
            totalNumItemsToProcess -= numItems;
            if(totalNumItemsToProcess > 0){
                if(totalNumItemsToProcess/1000 == 0) numItems = totalNumItemsToProcess % 1000;
                else numItems = 1000;
            }
            else break;
        }

        return jsonItemListObjs;
    }

    private JSONArray getJSONPropsList(D2PropCollection props){

        JSONArray jsonProps = new JSONArray();

        for(int i = 0; i < props.size(); i++){
            D2Prop currProp = (D2Prop) props.get(i);
            JSONObject jsonProp = new JSONObject();

            int[] pvals = currProp.getPVals();
            if(pvals != null){
                JSONArray jsonPvals = new JSONArray();
                for(int j = 0; j < pvals.length; j++) jsonPvals.add(pvals[j]);
                jsonProp.put("pVals", jsonPvals);
            } else{
                jsonProp.put("pVals", null);
            }
            jsonProp.put("pNum",  currProp.getPNum());
            jsonProp.put("funcN", currProp.getFuncN());
            jsonProp.put("qFlag", currProp.getQFlag());
            jsonProp.put("opApplied", currProp.getOpApplied());

            jsonProps.add(jsonProp);
        }

        return jsonProps;
    }

    private JSONArray getJSONSocketedItemsList(ArrayList socketedItems){

        JSONArray socketedItemList = new JSONArray();

        for(int i = 0; i < socketedItems.size(); i++){
            D2Item item = (D2Item) socketedItems.get(i);
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("fingerprint", item.getFingerprint());
            jsonItem.put("itemName", item.getItemName());
            jsonItem.put("itemType", item.getItemType());
            jsonItem.put("reqLvl", item.getReqLvl());
            jsonItem.put("Props", getJSONPropsList(item.getPropCollection()));
            socketedItemList.add(jsonItem);
        }

        return socketedItemList;
    }

    private JSONObject getJSONItemData(D2Item item){

        JSONObject jsonItemData = new JSONObject();

        jsonItemData.put("itemType", item.getItemType());
        jsonItemData.put("html", item.itemDumpHtml(false));
        jsonItemData.put("itemName", item.getItemName());
        jsonItemData.put("baseItemName", item.getBaseItemName());
        jsonItemData.put("itemCategory", item.getItemCategory());
        jsonItemData.put("itemKind", item.getItemKind());
        ///
        jsonItemData.put("width", item.get_width());
        jsonItemData.put("height", item.get_height());
        /////
        jsonItemData.put("socketed", item.isSocketed());
        jsonItemData.put("totalSockets", item.getSocketNrTotal());
        jsonItemData.put("socketsFilled", item.getSocketNrFilled());
        jsonItemData.put("level", item.getILvl());
        jsonItemData.put("quality", item.getItemQuality());
        jsonItemData.put("ethereal", item.isEthereal());
        jsonItemData.put("throwable", item.isThrowable());
        jsonItemData.put("stackable", item.isStackable());
        jsonItemData.put("identified", item.isIdentified());
        jsonItemData.put("currDur", item.getCurrDur());
        jsonItemData.put("maxDur", item.getMaxDur());
        jsonItemData.put("defense", item.getiDef());
        jsonItemData.put("cBlock", item.getBlock());
        jsonItemData.put("iBlock", item.getIBlock()); //think is chance to block
        jsonItemData.put("initDef", item.getInitDef());

        /*May look into getting damage later*/
        /*short[] dmg1 = item.getDmg1();
        if(dmg1 != null){
            JSONArray damage1 = new JSONArray();
            for(int i = 0; i < dmg1.length; i++) damage1.add(dmg1[i]);
            jsonItemData.put("Dmg1", damage1);
        } else{
            jsonItemData.put("Dmg1", null);
        }

        short[] dmg2 = item.getDmg2();
        if(dmg2 != null){
            JSONArray damage2 = new JSONArray();
            for(int i = 0; i < dmg2.length; i++) damage2.add(dmg2[i]);
            jsonItemData.put("Dmg2", damage2);
        } else{
            jsonItemData.put("Dmg2", null);
        }*/

        jsonItemData.put("reqLvl", item.getReqLvl());
        jsonItemData.put("reqStr", item.getReqStr());
        jsonItemData.put("reqDex", item.getReqDex());
        jsonItemData.put("stashPage", item.getStashPage());
        jsonItemData.put("setName", item.getSetName());
        jsonItemData.put("setID", item.getSetID());

        return jsonItemData;
    }


    private boolean generateJsonCharacter(){
        return false;
    }

}
