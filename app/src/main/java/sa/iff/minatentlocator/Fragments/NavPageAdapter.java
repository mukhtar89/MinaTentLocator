package sa.iff.minatentlocator.Fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Hashtable;

import sa.iff.minatentlocator.Dialogs.SwitchNetworkDialog;

/**
 * Created by mukht on 8/17/2016.
 */
public class NavPageAdapter extends FragmentPagerAdapter {

    private Hashtable<Integer, String> tabList = new Hashtable<>();
    private SwitchNetworkDialog switchNetworkDialog;

    public NavPageAdapter(FragmentManager fm, Context context, SwitchNetworkDialog switchNetworkDialog) {
        super(fm);
        this.switchNetworkDialog = switchNetworkDialog;
        tabList.put(0, "Mina");
        tabList.put(1, "Makkah");
        tabList.put(2, "Aziziyah");
    }

    @Override
    public Fragment getItem(int position) {
        return  ScrollViewFragment.newInstance(tabList.get(position), switchNetworkDialog);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position).toUpperCase();
    }
}
