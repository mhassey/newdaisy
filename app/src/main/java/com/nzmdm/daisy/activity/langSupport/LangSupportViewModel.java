package com.nzmdm.daisy.activity.langSupport;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.nzmdm.daisy.R;
import com.nzmdm.daisy.pojo.LangPojo;
import com.nzmdm.daisy.utils.Constraint;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose -  LangSupportViewModel is an view model class that help to set and get all language and selected language
 * Responsibility - Its main role is to save selected language from user and provide selected language and contains all language which support is given by our app
 **/
public class LangSupportViewModel extends AndroidViewModel {

    private List<LangPojo> langPojos;
    private RecyclerView.ViewHolder viewHolder;
    private LangPojo selectedLanguage;


    public LangSupportViewModel(@NonNull Application application) {
        super(application);

    }

    /**
     * Responsibility - getLangPojosForAdaptor method is used for return list of language
     * Parameters - No parameter
     **/
    public List<LangPojo> getLangPojosForAdaptor() {
        return langPojos;
    }


    /**
     * Responsibility - setLangData method is used for give support to predefine language
     * Parameters - No parameter
     **/
    public void setLangData(Context context)
    {
        List<LangPojo> langPojos=new ArrayList<>();
        LangPojo langPojo=new LangPojo();
        langPojo.setKey(Constraint.EN);
        langPojo.setValue(context.getString(R.string.english));
        langPojos.add(langPojo);
        LangPojo langPojo1=new LangPojo();
        langPojo1.setKey(Constraint.AR);
        langPojo1.setValue(context.getString(R.string.arabic));
        langPojos.add(langPojo1);

        LangPojo langPojo2=new LangPojo();
        langPojo2.setKey(Constraint.DA);
        langPojo2.setValue(context.getString(R.string.danish));
        langPojos.add(langPojo2);

        LangPojo langPojo3=new LangPojo();
        langPojo3.setKey(Constraint.DE);
        langPojo3.setValue(context.getString(R.string.dutch));
        langPojos.add(langPojo3);

        LangPojo langPojo4=new LangPojo();
        langPojo4.setKey(Constraint.ES);
        langPojo4.setValue(context.getString(R.string.spanish));
        langPojos.add(langPojo4);


        LangPojo langPojo5=new LangPojo();
        langPojo5.setKey(Constraint.FI);
        langPojo5.setValue(context.getString(R.string.finnish));
        langPojos.add(langPojo5);

        LangPojo langPojo6=new LangPojo();
        langPojo6.setKey(Constraint.FR);
        langPojo6.setValue(context.getString(R.string.french));
        langPojos.add(langPojo6);

        LangPojo langPojo7=new LangPojo();
        langPojo7.setKey(Constraint.HI);
        langPojo7.setValue(context.getString(R.string.hindi));
        langPojos.add(langPojo7);

        LangPojo langPojo8=new LangPojo();
        langPojo8.setKey(Constraint.IT);
        langPojo8.setValue(context.getString(R.string.italian));
        langPojos.add(langPojo8);

        LangPojo langPojo9=new LangPojo();
        langPojo9.setKey(Constraint.KO);
        langPojo9.setValue(context.getString(R.string.korean));
        langPojos.add(langPojo9);

        LangPojo langPojo10=new LangPojo();
        langPojo10.setKey(Constraint.NL);
        langPojo10.setValue(context.getString(R.string.dutch));
        langPojos.add(langPojo10);

        LangPojo langPojo11=new LangPojo();
        langPojo11.setKey(Constraint.NO);
        langPojo11.setValue(context.getString(R.string.norwegian));
        langPojos.add(langPojo11);

        LangPojo langPojo12=new LangPojo();
        langPojo12.setKey(Constraint.PL);
        langPojo12.setValue(context.getString(R.string.polish));
        langPojos.add(langPojo12);

        LangPojo langPojo13=new LangPojo();
        langPojo13.setKey(Constraint.PT);
        langPojo13.setValue(context.getString(R.string.portuguese));
        langPojos.add(langPojo13);

        LangPojo langPojo14=new LangPojo();
        langPojo14.setKey(Constraint.RU);
        langPojo14.setValue(context.getString(R.string.russian));
        langPojos.add(langPojo14);

        LangPojo langPojo15=new LangPojo();
        langPojo15.setKey(Constraint.SV);
        langPojo15.setValue(context.getString(R.string.swedish));
        langPojos.add(langPojo15);

        LangPojo langPojo16=new LangPojo();
        langPojo16.setKey(Constraint.TR);
        langPojo16.setValue(context.getString(R.string.turkish));
        langPojos.add(langPojo16);

        LangPojo langPojo17=new LangPojo();
        langPojo17.setKey(Constraint.UK);
        langPojo17.setValue(context.getString(R.string.ukrainian));
        langPojos.add(langPojo17);
        this.langPojos=langPojos;
    }


    /**
     * Responsibility - getViewHolder method is used for get recycle view viewholder object
     * Parameters - No parameter
     **/
    public RecyclerView.ViewHolder getViewHolder() {
        return viewHolder;
    }

    /**
     * Responsibility - getViewHolder method is used for set recycle view viewholder object
     * Parameters - Its takes RecyclerView.ViewHolder viewHolder object as parameter
     **/
    public void setViewHolder(RecyclerView.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public List<LangPojo> getLangPojos() {
        return langPojos;
    }

    public void setLangPojos(List<LangPojo> langPojos) {
        this.langPojos = langPojos;
    }

    /**
     * Responsibility - getSelectedLanguage method is used for get selected language
     * Parameters - No parameter
     **/
    public LangPojo getSelectedLanguage() {
        return selectedLanguage;
    }

    /**
     * Responsibility - setSelectedLanguage method is used for set selected language
     * Parameters - Its takes LangPojo object that contains selected language
     **/
    public void setSelectedLanguage(LangPojo selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }
}
