package com.news.demo.di.modules;

import androidx.lifecycle.ViewModel;

import com.news.demo.di.scope.ApplicationScope;
import com.news.demo.viewmodel.NewsViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @ApplicationScope
    @Binds
    @IntoMap
    @ViewModelKey(NewsViewModel.class)
    abstract ViewModel getWeatherViewModel(NewsViewModel viewModel);

}