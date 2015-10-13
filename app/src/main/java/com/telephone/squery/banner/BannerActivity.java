package com.telephone.squery.banner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;


public class BannerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnTouchListener {

    ViewPager pager;
    ImageView indicator1;
    ImageView indicator2;
    ImageView indicator3;
    ImageView indicator4;
    ImageView indicator5;

    public final int FAKE_SIZE = 100;
    private final int DEFAULT_SIZE = 5;
    private int mPosition = 0;
    private boolean mIsUserTouched = false;
    private int[] mImagesSrc = {
            R.mipmap.img1,
            R.mipmap.img2,
            R.mipmap.img3,
            R.mipmap.img4,
            R.mipmap.img5
    };

    private Timer mTimer = new Timer();//  轻量级 用timer 实现无限

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {

            //  主动循环  因为这里取反默认是true
            if (!mIsUserTouched) {
                mPosition = (mPosition + 1) % FAKE_SIZE;//  其实就是加1
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  这里不去设置 当他等于0的时候
                        if (mPosition == FAKE_SIZE - 1) {
                            mPosition = DEFAULT_SIZE - 1;
                            pager.setCurrentItem(mPosition, false);
                        }else {
                            pager.setCurrentItem(mPosition);
                        }
                    }
                });
            }
        }
    };

    private ImageView[] mIndicators ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        Log.d("BannerActivity", "test");
        initView();
        mTimer.schedule(mTimerTask, 5000, 3000);
    }

    @Override
    protected void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }

    private void initView() {
        pager = (ViewPager) findViewById(R.id.pager);
        indicator1 = (ImageView) findViewById(R.id.indicator1);
        indicator2 = (ImageView) findViewById(R.id.indicator2);
        indicator3 = (ImageView) findViewById(R.id.indicator3);
        indicator4 = (ImageView) findViewById(R.id.indicator4);
        indicator5 = (ImageView) findViewById(R.id.indicator5);
        mIndicators=new ImageView[]{indicator1,indicator2,indicator3,indicator4,indicator5};
        BannerAdapter adapter = new BannerAdapter(this);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(this);
        pager.setOnTouchListener(this);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 当滑动的时候跟小点去对应上实现联动
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {

        mPosition = position;//把 postion  给 mPosition 对应上
        int pos = position % DEFAULT_SIZE;
        for (ImageView indicator : mIndicators) {
            //  每次刚开始的时候都要保证 是没选中的颜色的   如果没有这个循环那样的话 只能改一次
            indicator.setImageResource(R.mipmap.indicator_unchecked);
        }
        // 在这里在修改固定的颜色
        mIndicators[pos].setImageResource(R.mipmap.indicator_checked);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 当我们人为的干涉的时候会触发这个方法
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_MOVE) {
            mIsUserTouched = true;
        } else if (action == MotionEvent.ACTION_UP) {
            mIsUserTouched = false;
        }
        return false;
    }

    private class BannerAdapter extends PagerAdapter {


        private final LayoutInflater mInflater;

        public BannerAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return FAKE_SIZE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = (ImageView)view.findViewById(R.id.pic);
            position = position % DEFAULT_SIZE;// 把大的数据 变成 小的数据
            imageView.setImageResource(mImagesSrc[position]);// (从0开始正好和 余数 对应上的)
            final int pos = position;// 图片加点击事件（这里是假数据）
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("BannerAdapter", "position : " + pos);
                }
            });
            container.addView(view); //必须在这里添加  不能直接将上面改成true
            return view;
        }

        /**
         * 这个方法是  在 startUpdate() instantiateItem() (once or more calls)   之后被调用
         */
        @Override
        public void finishUpdate(ViewGroup container) {

            int position = pager.getCurrentItem();
            Log.d("BannerAdapter", "finish update before, position=" + position);
            if (position == 0) {
                position = DEFAULT_SIZE;// 把0  余数+1的位置 正好还是0这张图片
                pager.setCurrentItem(position, false);
            } else if (position == FAKE_SIZE - 1) {
                position = DEFAULT_SIZE - 1;
                pager.setCurrentItem(position, false);
            }
            Log.d("BannerAdapter", "finish update after, position=" + position);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);//  删掉一个 是由于预加载了两个fragmet
        }
    }
}
