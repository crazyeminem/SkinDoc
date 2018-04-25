package com.dmt.skindoc;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.support.v4.view.ViewPager;
public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private MenuItem menuItem;
    private HomeFragment homeFragment;
    private RetrievalFragment retrievalFragment;
    private DiagnoseFragment diagnoseFragment;
    private PersonalFragment personalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeFragment=new HomeFragment();
        retrievalFragment=new RetrievalFragment();
        diagnoseFragment = new DiagnoseFragment();
        personalFragment=new PersonalFragment();
        viewPager=findViewById(R.id.home_viewPager);
        bottomNavigationView=findViewById(R.id.bottom_navigation_view);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.navigation_retrieval:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_diagnose:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.navigation_personal:
                        viewPager.setCurrentItem(3);
                        break;

                }
                return false;


            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);

    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(homeFragment);
        adapter.addFragment(retrievalFragment);
        adapter.addFragment(diagnoseFragment);
        adapter.addFragment(personalFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //retrievalFragment.onActivityResult(requestCode,resultCode,data);

    }

}
