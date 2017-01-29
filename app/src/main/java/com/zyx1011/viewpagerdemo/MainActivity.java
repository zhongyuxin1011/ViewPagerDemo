package com.zyx1011.viewpagerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    // 标题
    @Bind(R.id.text_view)
    TextView mTextView;
    // 点指示器
    @Bind(R.id.line_dot)
    LinearLayout mLineDot;

    private String[] mTitles;
    private ImageView[] mImages;
    private int[] mImagesId;

    // 记录当前ViewPager的position值，默认为0
    private int mCurrentDot;
    // 用于定时间隔轮播图片
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            // 自身调用
            mHandler.sendEmptyMessageDelayed(0, 1500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData(); // 初始化展示数据
        init(); // 初始化
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 开启轮播
        mHandler.sendEmptyMessageDelayed(0, 1500);
    }

    private void init() {
        ViewPagerAdapter adapter = new ViewPagerAdapter();
        mViewPager.setAdapter(adapter);

        // 设置ViewPager的初始位置
        int currentitem = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % mTitles.length);
        mViewPager.setCurrentItem(currentitem);
        // 设置点的状态和显示标题
        mLineDot.getChildAt(mCurrentDot).setEnabled(true);
        mTextView.setText(mTitles[mCurrentDot]);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 改变点的状态和标题，更新的position值
                mLineDot.getChildAt(mCurrentDot).setEnabled(false);
                mCurrentDot = position % mTitles.length;
                mLineDot.getChildAt(mCurrentDot).setEnabled(true);
                mTextView.setText(mTitles[mCurrentDot]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        // 取消自动轮播
                        mHandler.removeCallbacksAndMessages(null);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // 开启轮播
                        mHandler.sendEmptyMessageDelayed(0, 1500);
                        break;
                }
                return false;
            }
        });
    }

    private void initData() {
        mTitles = getResources().getStringArray(R.array.title);
        mImagesId = new int[]{R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};
        mImages = new ImageView[mTitles.length];
        for (int i = 0; i < mTitles.length; i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setBackgroundResource(mImagesId[i]);
            mImages[i] = imageView; // 存放图片的容器

            // 构建点指示器
            View dotView = new View(getApplicationContext());
            dotView.setBackgroundResource(R.drawable.dot_indicator);
            dotView.setEnabled(false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp2px(10),
                    dp2px(10));
            if (i != 0) {
                layoutParams.leftMargin = dp2px(5);
            }
            mLineDot.addView(dotView, layoutParams);
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // 整数的最大值，以实现轮播效果
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // 对position重新取余，修正值，避免数组索引越界异常
            int newPosition = position % mTitles.length;
            ImageView imageView = mImages[newPosition];
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }

    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    private int dp2px(float dp) {
        // 获取屏幕密度值
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }
}
