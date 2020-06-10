package com.example.athmavisionstream.mvvm;

import com.e60.mvvm.AppModule;
import com.example.athmavisionstream.RadioPlayerActivity;
import com.example.athmavisionstream.services.AudioService;

import javax.inject.Singleton;
import dagger.Component;

@Component(modules = {AppModule.class, BaseServiceModule.class})
@Singleton
public interface AppComponent {

    void doInjection(RadioPlayerActivity radioPlayerActivity);
    void doInjection(AudioService audioService);

}
