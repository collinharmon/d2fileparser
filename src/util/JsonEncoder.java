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

    public JSONObject generateJsonStash(){

        JSONObject obj = new JSONObject();

        obj.put("numItems", d2Stash.getNrItems());
        obj.put("isShared", d2Stash.isSharedStash());
        obj.put("numStashes", d2Stash.getNrStashes());
        obj.put("sharedGold", d2Stash.getSharedGold());
        obj.put("filename", d2Stash.getFilename());

        JSONArray itemList = getJSONItemList(d2Stash.getItemList());

        obj.put("Items", itemList);

        return obj;
    }

    private JSONArray getJSONItemList(ArrayList items){

        JSONArray jsonItems = new JSONArray();

        for(int i = 0; i < items.size(); i++){
            D2Item currItem = (D2Item) items.get(i);
            JSONObject jsonItem = new JSONObject();

            jsonItem.put("Props", getJSONPropsList(currItem.getPropCollection()));
            if(currItem.getSocketNrFilled() > 0)
                jsonItem.put("SocketedItems", getJSONSocketedItemsList(currItem.getSocketedItems()) );
            else
                jsonItem.put("SocketedItems", null);

            JSONObject jsonItemData = getJSONItemData(currItem);
            jsonItem.putAll(jsonItemData);

            jsonItems.add(jsonItem);
        }

        return jsonItems;
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

        jsonItemData.put("fingerprint", item.getFingerprint());
        jsonItemData.put("itemType", item.getItemType());
        jsonItemData.put("itemName", item.getItemName());
        jsonItemData.put("baseItemName", item.getBaseItemName());
        jsonItemData.put("width", item.get_width());
        jsonItemData.put("height", item.get_height());
        jsonItemData.put("socketed", item.isSocketed());
        jsonItemData.put("totalSockets", item.getSocketNrTotal());
        jsonItemData.put("socketsFilled", item.getSocketNrFilled());
        jsonItemData.put("lvl", item.getILvl());
        jsonItemData.put("quality", item.getItemQuality());
        jsonItemData.put("ethereal", item.isEthereal());
        jsonItemData.put("throwable", item.isThrowable());
        jsonItemData.put("magical", item.isMagical());
        jsonItemData.put("rare", item.isRare());
        jsonItemData.put("crafted", item.isCrafted());
        jsonItemData.put("set", item.isSet());
        jsonItemData.put("unique", item.isUnique());
        jsonItemData.put("runeword", item.isRuneWord());
        jsonItemData.put("smallcharm", item.isCharmSmall());
        jsonItemData.put("largecharm", item.isCharmLarge());
        jsonItemData.put("grandcharm", item.isCharmGrand());
        jsonItemData.put("jewel", item.isJewel());
        jsonItemData.put("gem", item.isGem());
        jsonItemData.put("stackable", item.isStackable());
        jsonItemData.put("rune", item.isRune());
        jsonItemData.put("typeMisc", item.isTypeMisc());
        jsonItemData.put("identified", item.isIdentified());
        jsonItemData.put("typeWeapon", item.isTypeWeapon());
        jsonItemData.put("typeArmor", item.isTypeArmor());
        jsonItemData.put("currDur", item.getCurrDur());
        jsonItemData.put("maxDur", item.getMaxDur());
        jsonItemData.put("defense", item.getiDef());
        jsonItemData.put("cBlock", item.getBlock());
        jsonItemData.put("iBlock", item.getIBlock());
        jsonItemData.put("initDef", item.getInitDef());
        
        short[] dmg1 = item.getDmg1();
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
        }

        jsonItemData.put("body", item.isBody());
        jsonItemData.put("belt", item.isBelt());
        jsonItemData.put("isChar", item.isCharacterItem());
        jsonItemData.put("reqLvl", item.getReqLvl());
        jsonItemData.put("reqStr", item.getReqStr());
        jsonItemData.put("reqDex", item.getReqDex());
        jsonItemData.put("stashPage", item.getStashPage());
        jsonItemData.put("setName", item.getSetName());
        jsonItemData.put("itemQuality", item.getItemQuality());
        jsonItemData.put("setID", item.getSetID());

        return jsonItemData;
    }


    private boolean generateJsonCharacter(){
        return false;
    }

}
