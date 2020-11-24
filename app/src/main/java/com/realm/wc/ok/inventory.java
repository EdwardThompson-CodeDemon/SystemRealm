package com.realm.wc.ok;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;
import com.realm.spartaservices.svars;

import java.io.Serializable;

import static com.realm.annotations.SyncDescription.service_type.Download;


/*
case "JobAllInventory":
				try {
					JSONObject jObj = new JSONObject(httpjobresponse);
					Log.v("OOOOOOOOOOOOO","MMMMMMMMMMMMMM "+jObj);
					JSONArray jObjResArray = jObj.getJSONArray("Result");
					for (int i=0; i<jObjResArray.length(); i++){
						JSONObject json_data = jObjResArray.getJSONObject(i);
						ContentValues AllinventoryItemTypes = new ContentValues();
						ContentValues inventoryItem = new ContentValues();
						ContentValues inventoryItem1 = new ContentValues();
						//inventory

						AllinventoryItemTypes.put("gridcollection_id", json_data.getInt("inventory_id")); //inventory_id
						AllinventoryItemTypes.put("inventory_id", json_data.getInt("inventory_id"));
						AllinventoryItemTypes.put("inv_itemtype_id", json_data.getInt("inventoryitemtypeid"));//inventory_item_type_id
						AllinventoryItemTypes.put("rid", json_data.getInt("inventory_id"));
						AllinventoryItemTypes.put("capacity", json_data.getString("capacity"));
						AllinventoryItemTypes.put("inv_name", json_data.getString("inventory_name"));//inventory_name
						AllinventoryItemTypes.put("inventory_make_name", json_data.getString("inventory_make_name"));
						AllinventoryItemTypes.put("bp", json_data.getDouble("current_buying_price"));
						AllinventoryItemTypes.put("transportcost", json_data.optDouble("transportation_cost", 0.0));
						AllinventoryItemTypes.put("routename", json_data.getString("inventory_item_type_name_route"));
						AllinventoryItemTypes.put("inventory_make_id", json_data.getString("inventory_make_id"));
						AllinventoryItemTypes.put("make_model_id", json_data.getInt("inventory_make_model_id"));
						AllinventoryItemTypes.put("make_model_name", json_data.getString("inventory_make_model_name"));
						AllinventoryItemTypes.put("blockId", json_data.getString("blockId"));
						AllinventoryItemTypes.put("blockname", json_data.getString("blockname"));
						AllinventoryItemTypes.put("moisture", json_data.getDouble("moisture_level"));
						AllinventoryItemTypes.put("isactive", json_data.getBoolean("isactive"));
						AllinventoryItemTypes.put("reduction_weight", json_data.getDouble("reduction_weight"));
						AllinventoryItemTypes.put("weight_per_reduction", json_data.getDouble("weight_per_reduction"));


						inventoryItem.put("rid", json_data.getInt("inventory_id"));
						inventoryItem.put("active", 1);//---------------
						inventoryItem.put("blockId", json_data.getString("blockId"));
						inventoryItem.put("blockname", json_data.getString("blockname"));
						inventoryItem.put("name", json_data.getString("inventory_name")+"  ["+json_data.getString("inventory_item_type_name_route")+"]");//---------------


						inventoryItem1.put("name", json_data.getString("inventory_name")+"  ["+json_data.getString("inventory_item_type_name_route")+"]");//---------------
						inventoryItem1.put("rid", json_data.getInt("inventory_id"));
						inventoryItem1.put("blockId", json_data.getString("blockId"));
						inventoryItem1.put("blockname", json_data.getString("blockname"));


						int updatingID = json_data.getInt("inventory_id");
						String member_id = null;

						Log.i("???>>>>>>  ", "+++++++++++"+updatingID);
						String[] WhereArgs1 = {""+updatingID};
						Cursor cursor1 = databaseservice.rawQuery("SELECT rid FROM "+ All_inv_dtls_Model.TABLE_NAME+" WHERE rid = ?", WhereArgs1);
						cursor1.moveToFirst();
						if(!cursor1.isAfterLast()){
							do{
								member_id = cursor1.getString(0);
							}while(cursor1.moveToNext());
						}
						cursor1.close();

						if(member_id == null){
							databaseservice.insert(All_inv_dtls_Model.TABLE_NAME, null, AllinventoryItemTypes);
							databaseservice.insert("inventory_items", null, inventoryItem);
						}else{
							String WhereClause = "rid = ?";
							String[] WhereArgs = {""+updatingID};
							try{
								databaseservice.update(All_inv_dtls_Model.TABLE_NAME, AllinventoryItemTypes, WhereClause, WhereArgs);
								databaseservice.update("inventory_items", inventoryItem1, WhereClause, WhereArgs);

							}catch(Exception ep){
								ep.printStackTrace();
							}
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//endService("JobAllInventory", e.getLocalizedMessage());
				}

				break;


 */
@DynamicClass(table_name = "TBL_all_inv_dtls")
@SyncDescription(service_name = "JobAllInventory",service_type = Download,download_link = svars.Inventory_download_link,use_download_filter = false)
public class inventory extends db_class_ implements Serializable {
/*
AllinventoryItemTypes.put("gridcollection_id", json_data.getInt("inventory_id")); //inventory_id
						AllinventoryItemTypes.put("inventory_id", json_data.getInt("inventory_id"));
						AllinventoryItemTypes.put("inv_itemtype_id", json_data.getInt("inventoryitemtypeid"));//inventory_item_type_id
						AllinventoryItemTypes.put("rid", json_data.getInt("inventory_id"));
						AllinventoryItemTypes.put("capacity", json_data.getString("capacity"));
						AllinventoryItemTypes.put("inv_name", json_data.getString("inventory_name"));//inventory_name
						AllinventoryItemTypes.put("inventory_make_name", json_data.getString("inventory_make_name"));
						AllinventoryItemTypes.put("bp", json_data.getDouble("current_buying_price"));
						AllinventoryItemTypes.put("transportcost", json_data.optDouble("transportation_cost", 0.0));
						AllinventoryItemTypes.put("routename", json_data.getString("inventory_item_type_name_route"));
						AllinventoryItemTypes.put("inventory_make_id", json_data.getString("inventory_make_id"));
						AllinventoryItemTypes.put("make_model_id", json_data.getInt("inventory_make_model_id"));
						AllinventoryItemTypes.put("make_model_name", json_data.getString("inventory_make_model_name"));
						AllinventoryItemTypes.put("blockId", json_data.getString("blockId"));
						AllinventoryItemTypes.put("blockname", json_data.getString("blockname"));
						AllinventoryItemTypes.put("moisture", json_data.getDouble("moisture_level"));
						AllinventoryItemTypes.put("isactive", json_data.getBoolean("isactive"));
						AllinventoryItemTypes.put("reduction_weight", json_data.getDouble("reduction_weight"));
						AllinventoryItemTypes.put("weight_per_reduction", json_data.getDouble("weight_per_reduction"));

 */

// {"$id":"3","id":208,"inventory_id":208,"inventory_name":"ARGAN FRUIT","capacity":"KGS","selling_price":0.0,"current_buying_price":5.0,"transportation_cost":0.0,"inventory_item_type_id":351,"inventory_item_type_name_route":"ARQAN ROUTE","inventory_make_model_id":0,"inventory_make_model_name":null,"inventory_make_id":0,"inventory_make_name":null,"weight_per_reduction":80.0,"weighbridge_moisture":0.00,"reduction_weight":1.0,"isactive":false,"moisture_level":0.0,"inventorytypeid":1,"inventoryitemtypeid":215,"synch_date":"0001-01-01T00:00:00","blockId":203,"isActiveRoute":true,"isCapagri":false,"blockname":"ARQAN A","transacting_branch_id":1,"active":true,"datecomparer":16051913828520704}
//    JobAllInventory::inventory_id=208 sync_status=1 transportcost=0 inventory_make_id=0 gridcollection_id=208 weight_per_reduction=80 moisture=0 make_model_id=0 capacity=KGS inv_itemtype_id=215 blockId=203 bp=5 rid=208 sid=208 isactive=false routename=ARQAN ROUTE inventory_make_name=null inv_name=ARGAN FRUIT reduction_weight=1 blockname=ARQAN A make_model_name=null sync_var=16051913828520704
    @DynamicProperty(json_key = "inventory_id", column_name = "gridcollection_id")
   public int gridcollection_id=0;

    @DynamicProperty(json_key = "inventory_id", column_name = "inventory_id")
    public int inventory_id=0;

    @DynamicProperty(json_key = "inventoryitemtypeid", column_name = "inv_itemtype_id")
    public String inv_itemtype_id="";


   @DynamicProperty(json_key = "inventory_id", column_name = "rid")
    public String rid="";


   @DynamicProperty(json_key = "capacity", column_name = "capacity")
    public String capacity="";


   @DynamicProperty(json_key = "inventory_name", column_name = "inv_name")
    public String inv_name="";


   @DynamicProperty(json_key = "inventory_make_name", column_name = "inventory_make_name")
    public String inventory_make_name="";


   @DynamicProperty(json_key = "current_buying_price", column_name = "bp")
    public String bp="";



@DynamicProperty(json_key = "inventory_item_type_name_route", column_name = "routename")
    public String routename="";



@DynamicProperty(json_key = "transportation_cost", column_name = "transportcost")
    public String transportcost="";



@DynamicProperty(json_key = "inventory_make_id", column_name = "inventory_make_id")
    public String inventory_make_id="";



@DynamicProperty(json_key = "inventory_make_model_id", column_name = "make_model_id")
    public String make_model_id="";



@DynamicProperty(json_key = "inventory_make_model_name", column_name = "make_model_name")
    public String make_model_name="";




@DynamicProperty(json_key = "blockId", column_name = "blockId")
    public String blockId="";




@DynamicProperty(json_key = "blockname", column_name = "blockname")
    public String blockname="";




@DynamicProperty(json_key = "moisture_level", column_name = "moisture")
    public String moisture="";




@DynamicProperty(json_key = "isactive", column_name = "isactive")
    public String isactive="";




@DynamicProperty(json_key = "reduction_weight", column_name = "reduction_weight")
    public String reduction_weight="";




@DynamicProperty(json_key = "weight_per_reduction", column_name = "weight_per_reduction")
    public String weight_per_reduction="";








    public inventory()
    {




    }

}
