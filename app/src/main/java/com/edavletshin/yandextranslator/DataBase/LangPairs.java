package com.edavletshin.yandextranslator.DataBase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by edgar on 15.03.2017.
 */

public class LangPairs {

    public static @NonNull String getKeyFromValue(HashMap<String,String> hashMap, String value) {

        for (String language : hashMap.keySet()) {
            if (hashMap.get(language).equals(value)) {
                return language;
            }
        }
        return "";
    }

    public static List<String> getSortedArray(HashMap<String,String> hashMap) {
        List<String> temp = new ArrayList<>();
        temp.addAll(hashMap.values());
        Collections.sort(temp);
        return temp;
    }

    public static final HashMap<String , String> pairsNames = new HashMap<String , String>() {{
        put("az","азербайджанский");
        put("sq","албанский");
        put("am","амхарский");
        put("en","английский");
        put("ar","арабский");
        put("hy","армянский");
        put("af","африкаанс");
        put("eu","баскский");
        put("ba","башкирский");
        put("be","белорусский");
        put("bn","бенгальский");
        put("bg","болгарский");
        put("bs","боснийский");
        put("cy","валлийский");
        put("hu","венгерский");
        put("vi","вьетнамский");
        put("ht","гаитянский (креольский)");
        put("gl","галисийский");
        put("nl","голландский");
        put("mrj","горномарийский");
        put("el","греческий");
        put("ka","грузинский");
        put("gu","гуджарати");
        put("da","датский");
        put("he","иврит");
        put("yi","идиш");
        put("id","индонезийский");
        put("ga","ирландский");
        put("it","итальянский");
        put("is","исландский");
        put("es","испанский");
        put("kk","казахский");
        put("kn","каннада");
        put("ca","каталанский");
        put("ky","киргизский");
        put("zh","китайский");
        put("ko","корейский");
        put("xh","коса");
        put("la","латынь");
        put("lv","латышский");
        put("lt","литовский");
        put("lb","люксембургский");
        put("mg","малагасийский");
        put("ms","малайский");
        put("ml","малаялам");
        put("mt","мальтийский");
        put("mk","македонский");
        put("mi","маори");
        put("mr","маратхи");
        put("mhr","марийский");
        put("mn","монгольский");
        put("de","немецкий");
        put("ne","непальский");
        put("no","норвежский");
        put("pa","панджаби");
        put("pap","папьяменто");
        put("fa","персидский");
        put("pl","польский");
        put("pt","португальский");
        put("ro","румынский");
        put("ru","русский");
        put("ceb","себуанский");
        put("sr","сербский");
        put("si","сингальский");
        put("sk","словацкий");
        put("sl","словенский");
        put("sw","суахили");
        put("su","сунданский");
        put("tg","таджикский");
        put("th","тайский");
        put("tl","тагальский");
        put("ta","тамильский");
        put("tt","татарский");
        put("te","телугу");
        put("tr","турецкий");
        put("udm","удмуртский");
        put("uz","узбекский");
        put("uk","украинский");
        put("ur","урду");
        put("fi","финский");
        put("fr","французский");
        put("hi","хинди");
        put("hr","хорватский");
        put("cs","чешский");
        put("sv","шведский");
        put("gd","шотландский");
        put("et","эстонский");
        put("eo","эсперанто");
        put("jv","яванский");
        put("ja","японский");
    }};
}


