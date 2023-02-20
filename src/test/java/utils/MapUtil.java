package utils;

import Core.UI.utils.PK_UI_Framework;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {
    public MapUtil(){}

    private static final Logger LOGGER = PK_UI_Framework.getLogger(MapUtil.class);
    private static Map<String, String> fieldMap = new HashMap<>();

    static {
        loadJsonPathMap();
    }

    private static void loadJsonPathMap() {
        applicantTableMappingEncryption();
        applicantTableMappingDecryption();
        NameFoundTableMappingDecryption();
        NameFoundTableMappingEncryption();
        TableMappingDecryption();
        TableMappingEncryption();
        EmployerTableMappingEncryption();
        EmployerTableMappingDecryption();
        ssnTableMappingEncryption();
        ssnTableMappingDecryption();
        emsopTableMappingDecryption();
        emsopTableMappingEncryption();
        addressTableMappingEncryption();
        addressTableMappingDecryption();
        workRequestTableMappingDecryption();
    }

    private static void applicantTableMappingEncryption() {
        String prefix = "applicant_table_enc#:#";
        fieldMap.put(prefix + "lastname", "lastName.encryptedString");
        fieldMap.put(prefix + "firstname", "firstName.encryptedString");
        fieldMap.put(prefix + "mname", "mname.encryptedString");
        fieldMap.put(prefix + "dob", "dob.encryptedString");
        fieldMap.put(prefix + "tel_no", "tel_no.encryptedString");
        fieldMap.put(prefix + "ssn", "ssn.encryptedString");
    }

    private static void applicantTableMappingDecryption() {
        String prefix = "applicant_table_dec#:#";
        fieldMap.put(prefix + "lastname", "lastName.plainText");
        fieldMap.put(prefix + "firstname", "firstName.plainText");
        fieldMap.put(prefix + "mname", "mname.plainText");
        fieldMap.put(prefix + "dob", "dob.plainText");
        fieldMap.put(prefix + "tel_no", "tel_no.plainText");
        fieldMap.put(prefix + "ssn", "ssn.plainText");
    }
    private static void NameFoundTableMappingEncryption() {
        String prefix = "namefound_table_enc#:#";
        fieldMap.put(prefix + "lastname", "lastname.encryptedString");
        fieldMap.put(prefix + "firstname", "firstnme.encryptedString");
        fieldMap.put(prefix + "mname", "mname.encryptedString");
        fieldMap.put(prefix + "dob", "dob.encryptedString");
        fieldMap.put(prefix + "ssn", "ssn.encryptedString");
    }

    private static void NameFoundTableMappingDecryption() {
        String prefix = "namefound_table_dec#:#";
        fieldMap.put(prefix + "lastname", "lastname.plainText");
        fieldMap.put(prefix + "firstname", "firstname.plainText");
        fieldMap.put(prefix + "mname", "mname.plainText");
        fieldMap.put(prefix + "dob", "dob.plainText");
        fieldMap.put(prefix + "ssn", "ssn.plainText");
    }

    private static void TableMappingEncryption() {
        String prefix = "table_enc#:#";
        fieldMap.put(prefix + "lastname", "lastname.encryptedString");
        fieldMap.put(prefix + "firstname", "firstname.encryptedString");
        fieldMap.put(prefix + "mname", "mname.encryptedString");
        fieldMap.put(prefix + "street", "street.encryptedString");
        fieldMap.put(prefix + "city", "city.encryptedString");
        fieldMap.put(prefix + "state", "state.encryptedString");
        fieldMap.put(prefix + "zip", "zip.encryptedString");
        fieldMap.put(prefix + "country", "country.encryptedString");
    }

    private static void TableMappingDecryption() {
        String prefix = "table_dec#:#";
        fieldMap.put(prefix + "lastname", "lastname.plainText");
        fieldMap.put(prefix + "firstname", "firstname.plainText");
        // fieldMap.put(prefix + "mname", "mname.plainText");
        fieldMap.put(prefix + "street", "street.plainText");
        fieldMap.put(prefix + "city", "city.plainText");
        fieldMap.put(prefix + "state", "state.plainText");
        fieldMap.put(prefix + "zip", "zip.plainText");
        // fieldMap.put(prefix + "country", "country.plainText");
    }

    private static void EmployerTableMappingEncryption() {
        String prefix = "employer_table_enc#:#";
        fieldMap.put(prefix + "employer", "employer.encryptedString");
    }

    private static void EmployerTableMappingDecryption() {
        String prefix = "employer_table_dec#:#";
        fieldMap.put(prefix + "employer", "employer.plainText");

    }

    private static void ssnTableMappingEncryption() {
        String prefix = "ssn_table_enc#:#";
        fieldMap.put(prefix + "ssn", "ssn.encryptedString");
        fieldMap.put(prefix + "Data", "Data.encryptedString");

    }

    private static void ssnTableMappingDecryption() {
        String prefix = "ssn_table_dec#:#";

        fieldMap.put(prefix + "ssn", "ssn.plainText");
        fieldMap.put(prefix + "Data", "Data.plainText");

    }

    private static void emsopTableMappingEncryption() {
        String prefix = "emsop_table_enc#:#";
        fieldMap.put(prefix + "caseno", "caseno.encryptedString");
        fieldMap.put(prefix + "courtname", "courtname.encryptedString");

    }

    private static void emsopTableMappingDecryption() {
        String prefix = "emsop_table_dec#:#";

        fieldMap.put(prefix + "caseno", "caseno.plainText");
        fieldMap.put(prefix + "courtname", "courtname.plainText");

    }
    private static void addressTableMappingEncryption() {
        String prefix = "address_table_enc#:#";
        fieldMap.put(prefix + "street", "street.encryptedString");
        fieldMap.put(prefix + "city", "city.encryptedString");
        fieldMap.put(prefix + "state", "state.encryptedString");
        fieldMap.put(prefix + "zip", "zip.encryptedString");

    }

    private static void addressTableMappingDecryption() {
        String prefix = "address_table_dec#:#";
        fieldMap.put(prefix + "street", "street.plainText");
        fieldMap.put(prefix + "city", "city.plainText");
        fieldMap.put(prefix + "state", "state.plainText");
        fieldMap.put(prefix + "zip", "zip.plainText");

    }

    private static void workRequestTableMappingDecryption() {
        String prefix = "workRequest_table_dec#:#";
        fieldMap.put(prefix + "requestor_name", "requestor_name.plainText");
        fieldMap.put(prefix + "requestor_email", "requestor_email.plainText");
        fieldMap.put(prefix + "requestor_phone", "requestor_phone.plainText");
        fieldMap.put(prefix + "requestor_fax", "requestor_fax.plainText");

    }

    public static String getMappingValue(String prefix, String attribute) {
        return fieldMap.get(prefix + "#:#" + attribute);
    }


}
