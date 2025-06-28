package com.allvens.allworkouts.settings_manager.TextDocumentation;

import android.content.Context;

import com.allvens.allworkouts.R;

public class TextDocumentation_TermsOfService extends textDocumentationManager {

    public TextDocumentation_TermsOfService(Context context) {
        super(context);

        createTitle(R.string.terms_of_use_title);
        createParagraph(R.string.terms_of_use_date);

        createParagraph(R.string.terms_of_use_summary_1);
        createParagraph(R.string.terms_of_use_summary_2);

        OrderList olContainer_Pos = new OrderList();

        createSubTitle(olContainer_Pos.getNextPos() + getTextFromR(R.string.terms_of_use_section_2_sub_title));
        createParagraph(R.string.terms_of_use_section_2_body);

        createSubTitle(olContainer_Pos.getNextPos() + getTextFromR(R.string.terms_of_use_section_3_sub_title));
        createParagraph(R.string.terms_of_use_section_3_body);

        OrderList olRelease_Alpha = new OrderList();
        createSubTitle(olContainer_Pos.getNextPos() + getTextFromR(R.string.terms_of_use_section_4_sub_title));
        createParagraph(olRelease_Alpha.get_NextAlpa() + getTextFromR(R.string.terms_of_use_section_4_body_1));
        createParagraph(olRelease_Alpha.get_NextAlpa() + getTextFromR(R.string.terms_of_use_section_4_body_2));

        createSubTitle(olContainer_Pos.getNextPos() + getTextFromR(R.string.terms_of_use_section_5_sub_title));
        createParagraph(R.string.terms_of_use_section_5_body);
    }
}
