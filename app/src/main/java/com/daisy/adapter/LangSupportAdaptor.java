package com.daisy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.daisy.R;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.LangSupportCustomLayoutBinding;
import com.daisy.interfaces.LangSupportCallBack;
import com.daisy.pojo.LangPojo;

import java.util.List;
/**
 * Its an adaptor that connect with language list and what ever data it has show to user
 **/
public class LangSupportAdaptor extends RecyclerView.Adapter<LangSupportAdaptor.ViewHolder> {
    private LangSupportCallBack callBack;
    private List<LangPojo> langPojos;
    private SessionManager sessionManager;


    public LangSupportAdaptor(List<LangPojo> langPojos, LangSupportCallBack callBack) {
        this.callBack = callBack;
        this.langPojos = langPojos;
        this.sessionManager = SessionManager.get();
    }

    @NonNull
    @Override
    public LangSupportAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LangSupportCustomLayoutBinding langSupportCustomLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.lang_support_custom_layout, parent, false);

        return new ViewHolder(langSupportCustomLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LangSupportAdaptor.ViewHolder holder, int position) {
        LangPojo langPojo = langPojos.get(position);
        String defaultValue = sessionManager.getLang();
        if (langPojo.getKey().equals(defaultValue)) {
            holder.langSupportCustomLayoutBinding.selectLang.setVisibility(View.VISIBLE);
            callBack.setDefaultLang(langPojo, holder);
        }
        holder.langSupportCustomLayoutBinding.language.setText(langPojo.getValue());
        holder.langSupportCustomLayoutBinding.headerLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LangPojo langPojo = langPojos.get(position);
                LangSupportAdaptor.ViewHolder holder1 = (ViewHolder) callBack.getHolder();
                if (holder1 != null) {
                    holder1.langSupportCustomLayoutBinding.selectLang.setVisibility(View.INVISIBLE);

                }
                holder.langSupportCustomLayoutBinding.selectLang.setVisibility(View.VISIBLE);
                callBack.setLangPojo(langPojo, holder);
            }
        });

    }


    @Override
    public int getItemCount() {
        return langPojos.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public LangSupportCustomLayoutBinding langSupportCustomLayoutBinding;

        public ViewHolder(@NonNull LangSupportCustomLayoutBinding itemView) {
            super(itemView.getRoot());
            this.langSupportCustomLayoutBinding = itemView;

        }
    }

}
