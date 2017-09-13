package anwar.metroim.PhoneContactSynchronization;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anwar.metroim.Adapter.RowItem;
import anwar.metroim.LocalHandeler.DatabaseHandler;

/**
 * Created by anwar on 12/17/2016.
 */

public class PhoneContacts implements IphoneContacts {
    public  static  List<String> list=new ArrayList<>();
    public  static  List<RowItem> AlreadySyncsContactlist=new ArrayList<>();
    public  static  List<RowItem> MetroImContactList=new ArrayList<>();
    public static  String json;
    public static   String []MContactNumber=new String[150];
    public static String string;
    String countryISOCode;
    String mPhoneNumber;
    private  String b;
    public String getAllContactNumber(Context context) throws Exception {
        DatabaseHandler databaseHandler=new DatabaseHandler(context);
        list.clear();
        AlreadySyncsContactlist.clear();
        MetroImContactList.clear();
       AlreadySyncsContactlist=databaseHandler.getContact(2);
        Cursor phones =context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext()) {
            String OrginalNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String phoneNumber = OrginalNumber;
            phoneNumber=CheckNumber(context,phoneNumber);
            if (phoneNumber.length() > 12 && AllreadySync(phoneNumber))
            {
                list.add(phoneNumber+ ";" + OrginalNumber);
                AlreadySyncsContactlist.add(new RowItem(phoneNumber));
            }
        }
        phones.close();
        JSONArray ar=new JSONArray(list);
        json=ar.toString();
        return json;
    }

    public boolean AllreadySync(String number){
        Boolean returnType=true;
        for(int i=0;i<AlreadySyncsContactlist.size();i++)
        {
            if(AlreadySyncsContactlist.get(i).getContact_number().equals(number))
            {
                returnType=false;
                break;
            }
        }
        return returnType;
    }

    public String CheckNumber(Context context, String number){
        number=number.replaceAll("[^+0-9]", "");
        number=number.replaceAll(" ","");
        if (number.startsWith("0") && (number.length()) == 11) {
            number = number.replaceFirst("0", getPrefixCountyCode(context));
        }
        if (number.startsWith("00") && (number.length()) == 14) {
            number =number.replaceFirst("00", "+");
        }
        return number;
    }
    public String getPrefixCountyCode(Context context){
        Map<String, String> CountryCode=new HashMap<>();
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();
        if (tMgr != null){
            countryISOCode = tMgr.getSimCountryIso().toUpperCase();
        }
        CountryCode.put("AC", "+247");
        CountryCode.put("AD", "+376");
        CountryCode.put("AE", "+971");
        CountryCode.put("AF", "+93");
        CountryCode.put("AG", "+1-268");
        CountryCode.put("AI", "+1-264");
        CountryCode.put("AL", "+355");
        CountryCode.put("AM", "+374");
        CountryCode.put("AN", "+599");
        CountryCode.put("AO", "+244");
        CountryCode.put("AR", "+54");
        CountryCode.put("AS", "+1-684");
        CountryCode.put("AT", "+43");
        CountryCode.put("AU", "+61");
        CountryCode.put("AW", "+297");
        CountryCode.put("AX", "+358-18");
        CountryCode.put("AZ", "+374-97");
        CountryCode.put("AZ", "+994");
        CountryCode.put("BA", "+387");
        CountryCode.put("BB", "+1-246");
        CountryCode.put("BD", "+880");
        CountryCode.put("BE", "+32");
        CountryCode.put("BF", "+226");
        CountryCode.put("BG", "+359");
        CountryCode.put("BH", "+973");
        CountryCode.put("BI", "+257");
        CountryCode.put("BJ", "+229");
        CountryCode.put("BM", "+1-441");
        CountryCode.put("BN", "+673");
        CountryCode.put("BO", "+591");
        CountryCode.put("BR", "+55");
        CountryCode.put("BS", "+1-242");
        CountryCode.put("BT", "+975");
        CountryCode.put("BW", "+267");
        CountryCode.put("BY", "+375");
        CountryCode.put("BZ", "+501");
        CountryCode.put("CA", "+1");
        CountryCode.put("CC", "+61");
        CountryCode.put("CD", "+243");
        CountryCode.put("CF", "+236");
        CountryCode.put("CG", "+242");
        CountryCode.put("CH", "+41");
        CountryCode.put("CI", "+225");
        CountryCode.put("CK", "+682");
        CountryCode.put("CL", "+56");
        CountryCode.put("CM", "+237");
        CountryCode.put("CN", "+86");
        CountryCode.put("CO", "+57");
        CountryCode.put("CR", "+506");
        CountryCode.put("CS", "+381");
        CountryCode.put("CU", "+53");
        CountryCode.put("CV", "+238");
        CountryCode.put("CX", "+61");
        CountryCode.put("CY", "+90-392");
        CountryCode.put("CY", "+357");
        CountryCode.put("CZ", "+420");
        CountryCode.put("DE", "+49");
        CountryCode.put("DJ", "+253");
        CountryCode.put("DK", "+45");
        CountryCode.put("DM", "+1-767");
        CountryCode.put("DO", "+1-809"); // and 1-829?
        CountryCode.put("DZ", "+213");
        CountryCode.put("EC", "+593");
        CountryCode.put("EE", "+372");
        CountryCode.put("EG", "+20");
        CountryCode.put("EH", "+212");
        CountryCode.put("ER", "+291");
        CountryCode.put("ES", "+34");
        CountryCode.put("ET", "+251");
        CountryCode.put("FI", "+358");
        CountryCode.put("FJ", "+679");
        CountryCode.put("FK", "+500");
        CountryCode.put("FM", "+691");
        CountryCode.put("FO", "+298");
        CountryCode.put("FR", "+33");
        CountryCode.put("GA", "+241");
        CountryCode.put("GB", "+44");
        CountryCode.put("GD", "+1-473");
        CountryCode.put("GE", "+995");
        CountryCode.put("GF", "+594");
        CountryCode.put("GG", "+44");
        CountryCode.put("GH", "+233");
        CountryCode.put("GI", "+350");
        CountryCode.put("GL", "+299");
        CountryCode.put("GM", "+220");
        CountryCode.put("GN", "+224");
        CountryCode.put("GP", "+590");
        CountryCode.put("GQ", "+240");
        CountryCode.put("GR", "+30");
        CountryCode.put("GT", "+502");
        CountryCode.put("GU", "+1-671");
        CountryCode.put("GW", "+245");
        CountryCode.put("GY", "+592");
        CountryCode.put("HK", "+852");
        CountryCode.put("HN", "+504");
        CountryCode.put("HR", "+385");
        CountryCode.put("HT", "+509");
        CountryCode.put("HU", "+36");
        CountryCode.put("ID", "+62");
        CountryCode.put("IE", "+353");
        CountryCode.put("IL", "+972");
        CountryCode.put("IM", "+44");
        CountryCode.put("IN", "+91");
        CountryCode.put("IO", "+246");
        CountryCode.put("IQ", "+964");
        CountryCode.put("IR", "+98");
        CountryCode.put("IS", "+354");
        CountryCode.put("IT", "+39");
        CountryCode.put("JE", "+44");
        CountryCode.put("JM", "+1-876");
        CountryCode.put("JO", "+962");
        CountryCode.put("JP", "+81");
        CountryCode.put("KE", "+254");
        CountryCode.put("KG", "+996");
        CountryCode.put("KH", "+855");
        CountryCode.put("KI", "+686");
        CountryCode.put("KM", "+269");
        CountryCode.put("KN", "+1-869");
        CountryCode.put("KP", "+850");
        CountryCode.put("KR", "+82");
        CountryCode.put("KW", "+965");
        CountryCode.put("KY", "+1-345");
        CountryCode.put("KZ", "+7");
        CountryCode.put("LA", "+856");
        CountryCode.put("LB", "+961");
        CountryCode.put("LC", "+1-758");
        CountryCode.put("LI", "+423");
        CountryCode.put("LK", "+94");
        CountryCode.put("LR", "+231");
        CountryCode.put("LS", "+266");
        CountryCode.put("LT", "+370");
        CountryCode.put("LU", "+352");
        CountryCode.put("LV", "+371");
        CountryCode.put("LY", "+218");
        CountryCode.put("MA", "+212");
        CountryCode.put("MC", "+377");
        CountryCode.put("MD", "+373-533");
        CountryCode.put("MD", "+373");
        CountryCode.put("ME", "+382");
        CountryCode.put("MG", "+261");
        CountryCode.put("MH", "+692");
        CountryCode.put("MK", "+389");
        CountryCode.put("ML", "+223");
        CountryCode.put("MM", "+95");
        CountryCode.put("MN", "+976");
        CountryCode.put("MO", "+853");
        CountryCode.put("MP", "+1-670");
        CountryCode.put("MQ", "+596");
        CountryCode.put("MR", "+222");
        CountryCode.put("MS", "+1-664");
        CountryCode.put("MT", "+356");
        CountryCode.put("MU", "+230");
        CountryCode.put("MV", "+960");
        CountryCode.put("MW", "+265");
        CountryCode.put("MX", "+52");
        CountryCode.put("MY", "+60");
        CountryCode.put("MZ", "+258");
        CountryCode.put("NA", "+264");
        CountryCode.put("NC", "+687");
        CountryCode.put("NE", "+227");
        CountryCode.put("NF", "+672");
        CountryCode.put("NG", "+234");
        CountryCode.put("NI", "+505");
        CountryCode.put("NL", "+31");
        CountryCode.put("NO", "+47");
        CountryCode.put("NP", "+977");
        CountryCode.put("NR", "+674");
        CountryCode.put("NU", "+683");
        CountryCode.put("NZ", "+64");
        CountryCode.put("OM", "+968");
        CountryCode.put("PA", "+507");
        CountryCode.put("PE", "+51");
        CountryCode.put("PF", "+689");
        CountryCode.put("PG", "+675");
        CountryCode.put("PH", "+63");
        CountryCode.put("PK", "+92");
        CountryCode.put("PL", "+48");
        CountryCode.put("PM", "+508");
        CountryCode.put("PR", "+1-787"); // and 1-939 ?
        CountryCode.put("PS", "+970");
        CountryCode.put("PT", "+351");
        CountryCode.put("PW", "+680");
        CountryCode.put("PY", "+595");
        CountryCode.put("QA", "+974");
        CountryCode.put("RE", "+262");
        CountryCode.put("RO", "+40");
        CountryCode.put("RS", "+381");
        CountryCode.put("RU", "+7");
        CountryCode.put("RW", "+250");
        CountryCode.put("SA", "+966");
        CountryCode.put("SB", "+677");
        CountryCode.put("SC", "+248");
        CountryCode.put("SD", "+249");
        CountryCode.put("SE", "+46");
        CountryCode.put("SG", "+65");
        CountryCode.put("SH", "+290");
        CountryCode.put("SI", "+386");
        CountryCode.put("SJ", "+47");
        CountryCode.put("SK", "+421");
        CountryCode.put("SL", "+232");
        CountryCode.put("SM", "+378");
        CountryCode.put("SN", "+221");
        CountryCode.put("SO", "+252");
        CountryCode.put("SO", "+252");
        CountryCode.put("SR", "+597");
        CountryCode.put("ST", "+239");
        CountryCode.put("SV", "+503");
        CountryCode.put("SY", "+963");
        CountryCode.put("SZ", "+268");
        CountryCode.put("TA", "+290");
        CountryCode.put("TC", "+1-649");
        CountryCode.put("TD", "+235");
        CountryCode.put("TG", "+228");
        CountryCode.put("TH", "+66");
        CountryCode.put("TJ", "+992");
        CountryCode.put("TK", "+690");
        CountryCode.put("TL", "+670");
        CountryCode.put("TM", "+993");
        CountryCode.put("TN", "+216");
        CountryCode.put("TO", "+676");
        CountryCode.put("TR", "+90");
        CountryCode.put("TT", "+1-868");
        CountryCode.put("TV", "+688");
        CountryCode.put("TW", "+886");
        CountryCode.put("TZ", "+255");
        CountryCode.put("UA", "+380");
        CountryCode.put("UG", "+256");
        CountryCode.put("US", "+1");
        CountryCode.put("UY", "+598");
        CountryCode.put("UZ", "+998");
        CountryCode.put("VA", "+379");
        CountryCode.put("VC", "+1-784");
        CountryCode.put("VE", "+58");
        CountryCode.put("VG", "+1-284");
        CountryCode.put("VI", "+1-340");
        CountryCode.put("VN", "+84");
        CountryCode.put("VU", "+678");
        CountryCode.put("WF", "+681");
        CountryCode.put("WS", "+685");
        CountryCode.put("YE", "+967");
        CountryCode.put("YT", "+262");
        CountryCode.put("ZA", "+27");
        CountryCode.put("ZM", "+260");
        CountryCode.put("ZW", "+263");
        String Countrycode=CountryCode.get(countryISOCode);
        if (countryISOCode == null) {
            //if country null then ......... Country code con't be null
            throw new IllegalArgumentException("Unknown country code " );
        }
        return  Countrycode;
    }
}

