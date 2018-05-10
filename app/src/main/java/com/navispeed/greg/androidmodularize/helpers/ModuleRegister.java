package com.navispeed.greg.androidmodularize.helpers;

import com.navispeed.greg.common.Module;
import com.navispeed.greg.goodbye.GoodbyeModule;
import com.navispeed.greg.welcome.WelcomeModule;
import jonas.emile.agora.AgoraModule;
import jonas.emile.events.EventsModule;
import jonas.emile.login.LoginModule;
import jonas.emile.news.NewsModule;
import jonas.emile.poll.PollModule;
import jonas.emile.reports.ReportsModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by greg on 04/06/2017.
 */
public class ModuleRegister {

    private static ModuleRegister ourInstance = new ModuleRegister();

    public static ModuleRegister getInstance() {
        return ourInstance;
    }

    private List<Module> moduleList = new ArrayList<>();

    private ModuleRegister() {
        this.moduleList.add(WelcomeModule.getInstance());
        this.moduleList.add(GoodbyeModule.getInstance());
        this.moduleList.add(EventsModule.getInstance());
        this.moduleList.add(LoginModule.getInstance());
        this.moduleList.add(NewsModule.getInstance());
        this.moduleList.add(ReportsModule.getInstance());
        this.moduleList.add(AgoraModule.getInstance());
        this.moduleList.add(PollModule.getInstance());
    }

    public final List<Module> getModuleList() {
        return moduleList;
    }
}

