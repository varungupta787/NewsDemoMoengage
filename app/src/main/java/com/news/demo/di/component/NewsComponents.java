package com.news.demo.di.component;


import com.news.demo.di.modules.NetworkModule;
import com.news.demo.di.modules.ViewModelModule;
import com.news.demo.di.scope.ApplicationScope;
import com.news.demo.ui.activity.NewsActivity;

import dagger.Component;

@ApplicationScope
@Component(modules = {NetworkModule.class, ViewModelModule.class})
public interface NewsComponents {
    void injectNewsActivity(NewsActivity activity);
}
