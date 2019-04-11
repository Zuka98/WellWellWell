package com.NHS.UCLTeam9.WellWellWell;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchModelForPostcode implements Searchable {
    private String mTitle;

    public SearchModelForPostcode(String title) {
        mTitle = title;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public SearchModelForPostcode setTitle(String title) {
        mTitle = title;
        return this;
    }
}