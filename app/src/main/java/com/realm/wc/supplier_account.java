package com.realm.wc;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;
import com.realm.spartaservices.svars;

import java.io.Serializable;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Upload;

/*
JSONObject json_data = jObjResArray.getJSONObject(i);
							ContentValues SupplierAccounts = new ContentValues();

							SupplierAccounts.put("rid", json_data.getInt("$id"));
							SupplierAccounts.put("member_id", json_data.getInt("member_id"));
							SupplierAccounts.put("account_id", json_data.getInt("acc_id"));
							SupplierAccounts.put("ref_id", json_data.getInt("acc_id"));
							SupplierAccounts.put("acc_name", json_data.getString("acc_name"));
							SupplierAccounts.put("sync_datetime", json_data.getString("datecomparer"));

							int updatingID = json_data.getInt("acc_id");
							String member_id = null;
							String[] WhereArgs1 = {""+updatingID};
							Cursor cursor1 = databaseservice.rawQuery("SELECT ref_id FROM TBL_supplier_account WHERE ref_id = ?", WhereArgs1);
							cursor1.moveToFirst();
							if(!cursor1.isAfterLast()){
								do{
									member_id = cursor1.getString(0);
								}while(cursor1.moveToNext());

							}
							cursor1.close();

							if(member_id == null){
								Log.i("???>>>>>>  ", "++++++++++%%%%%%%%%%%%%%%%%%%%% member accounts");
								databaseservice.insert("TBL_supplier_account", null, SupplierAccounts);
							}else{
								String WhereClause = "ref_id = ?";
								String[] WhereArgs = {""+updatingID};
								Log.i("???>>>>>>  ", "++++++++++%%%%%%%%%%%%%%%%%%%%%"+updatingID);
								try{
									databaseservice.update("TBL_supplier_account", SupplierAccounts, WhereClause, WhereArgs);
								}catch(Exception ep){
									ep.printStackTrace();
								}
							}
 */
@DynamicClass(table_name = "TBL_supplier_account")
@SyncDescription(service_name = "JobSupplierAccs",service_type = Download,download_link = svars.Supply_account_download_link,chunk_size =svars.excuse_request_limit )
public class supplier_account extends db_class_ implements Serializable {






  @DynamicProperty(json_key = "member_id")
   public String member_id="";

    @DynamicProperty(json_key = "acc_id")
    public String account_id="";

    @DynamicProperty(json_key = "acc_name")
    public String acc_name="";

 @DynamicProperty(json_key = "synch_date")
    public String sync_datetime="";




    public supplier_account()
    {




    }

}
