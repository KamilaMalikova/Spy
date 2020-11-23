package com.spy.spydemo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class UserApplications {
    private Context context;
    private PackageManager pm;
    public String path;
    public UserApplications(Context context) {
        this.context = context;
        this.pm = context.getPackageManager();
    }

    public List<ApplicationInfo> getListOfApplications(PackageManager pm) {
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
       return packages;
    }
    //Метод получает список всех приложений и сохраняет его в файл apps внутри приватного каталога приложения.
    public void saveToFile(){
        String appsFile = context.getApplicationInfo().dataDir + "/apps";
        this.path = appsFile;
        try {
            List<ApplicationInfo> packages = getListOfApplications(this.pm);
            PrintWriter pw = new PrintWriter(new File(appsFile));

            for (ApplicationInfo packageInfo : packages) {
                if (!isSystemPackage(packageInfo))
                    pw.println(pm.getApplicationLabel(packageInfo) + ": " + packageInfo.packageName);
            }

            pw.close();
        } catch (IOException e) {}

    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
}
