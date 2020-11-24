package com.realm.wc;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;
import com.realm.spartaservices.svars;

import java.io.Serializable;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Upload;


@DynamicClass(table_name = "dispatch_session")
@SyncDescription(service_name = "InsertReceivingSession", service_type = Upload, download_link = svars.Receiving_session_upload_link)

public class receiving_session extends db_class_ implements Serializable {

    @DynamicProperty(json_key = "vehicle_registration_id", column_name = "vehicle_id")
    public String vehicle_registration_id="";

    @DynamicProperty(json_key = "session", column_name = "session_no")
    public String session="";

    @DynamicProperty(json_key = "driver_name", column_name = "driver_name")
    public String driver_name="";

    @DynamicProperty(json_key = "driver_telephone", column_name = "driver_telephone")
    public String driver_telephone="";

    public receiving_session() {
    }
}
