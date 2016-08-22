package sa.iff.minatentlocator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Hashtable;

import sa.iff.minatentlocator.Activities.MainActivity;

/**
 * Created by mukht on 8/17/2016.
 */
public class NavPageAdapter extends FragmentPagerAdapter {

    private Hashtable<Integer, String> tabList = new Hashtable<>();
    private Context context;

    public NavPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        tabList.put(0, "Mina");
        tabList.put(1, "Makkah");
        tabList.put(2, "Aziziyah");
    }

    @Override
    public Fragment getItem(int position) {
        return NavFragment.newInstance(tabList.get(position));
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
