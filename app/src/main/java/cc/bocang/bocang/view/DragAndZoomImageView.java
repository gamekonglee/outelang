package cc.bocang.bocang.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.ui.DiyActivity;

/****
 * 这里你要明白几个方法执行的流程： 首先ImageView是继承自View的子类.
 * onLayout方法：是一个回调方法.该方法会在在View中的layout方法中执行，在执行layout方法前面会首先执行setFrame方法.
 * layout方法：
 * setFrame方法：判断我们的View是否发生变化，如果发生变化，那么将最新的l，t，r，b传递给View，然后刷新进行动态更新UI.
 * 并且返回ture.没有变化返回false.
 * <p/>
 * invalidate方法：用于刷新当前控件,
 */
public class DragAndZoomImageView extends ImageView {

    private int bitmap_W, bitmap_H;// 当前图片宽高

    private int MAX_W, MIN_W;// 极限值

    private int current_Top, current_Right, current_Bottom, current_Left;// 当前图片上下左右坐标

    private int start_Top = -1, start_Right = -1, start_Bottom = -1,
            start_Left = -1;// 初始化默认位置.

    private int start_x, start_y, current_x, current_y;// 触摸位置

    private float beforeLenght, afterLenght;// 两触点距离

    private float scale_temp;// 缩放比例

    private FrameLayout parentContainer;
    private FrameLayout sonContainer;

    private String mBrandInfo;// 该灯的品牌位置信息
    private Spanned mLightInfo;// 该灯的参数信息
    private String mBrandName;// 该灯的品牌名称
    private String mLightName;// 该灯的名称
    private int mLightCount; // 该灯的编号
    private int selectProductId;//该灯的id（数据库里的id,我的产品的灯id变为-1）
    private int shareProductId;//该灯的id（数据库里的id）

    public int getShareProductId() {
        return shareProductId;
    }

    public void setShareProductId(int shareProductId) {
        this.shareProductId = shareProductId;
    }

    public int getSelectProductId() {
        return selectProductId;
    }

    public void setSelectProductId(int selectProductId) {
        this.selectProductId = selectProductId;
    }

    public String getmBrandName() {
        return mBrandName;
    }

    public void setmBrandName(String mBrandName) {
        this.mBrandName = mBrandName;
    }

    public String getmLightName() {
        return mLightName;
    }

    public void setmLightName(String mLightName) {
        this.mLightName = mLightName;
    }

    public void setmBrandInfo(String brandInfo) {
        this.mBrandInfo = brandInfo;
    }

    public void setmLightInfo(Spanned mLightInfo) {
        this.mLightInfo = mLightInfo;
    }

    public void setmLightCount(int mLightCount) {
        this.mLightCount = mLightCount;
    }

    /**
     * 模式 NONE：无 DRAG：拖拽. ZOOM:缩放
     */
    private enum MODE {
        NONE, DRAG, ZOOM

    }

    ;

    private MODE mode = MODE.NONE;// 默认模式

    private Context mContext;

    private GestureDetector mGestureDetector;// 手势监听变量

    public DragAndZoomImageView(Context context) {
        super(context);
        this.mContext = context;
        mGestureDetector = new GestureDetector(context, new MyGestureListener());// 注册手势监听器
    }

    public DragAndZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mGestureDetector = new GestureDetector(context, new MyGestureListener());// 注册手势监听器
    }

    /***
     * 设置显示图片
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        /** 获取图片宽高 **/
        bitmap_W = bm.getWidth();
        bitmap_H = bm.getHeight();

        MAX_W = bitmap_W * 2;
        MIN_W = bitmap_W / 10;

        super.setImageBitmap(bm);

    }

    // 把其容器对象传进来
    public void setContainer(FrameLayout parentContainer,
                             FrameLayout sunContainer) {
        this.parentContainer = parentContainer;
        this.sonContainer = sunContainer;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (start_Top == -1) {
            start_Top = top;
            start_Left = left;
            start_Bottom = bottom;
            start_Right = right;
        }

    }

    /**
     * 处理单点、多点触摸
     **/
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {// 多点触摸，获取下一个动作

            case MotionEvent.ACTION_DOWN:

                // //设置ViewPager不可滑动
                // ((DiyActivity)mContext).mViewPager.requestDisallowInterceptTouchEvent(true);
                onTouchDown(event);

                break;
            // 另一个触摸点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                onPointerDown(event);

                break;

            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);

                break;
            case MotionEvent.ACTION_UP:
                // 设置ViewPager可以滑动
                // ((SceneActivity)mContext).mViewPager.setScrollble(true);
                mode = MODE.NONE;

                // 无论单点还是多点触摸都会触发，在此处理选中灯的信息

                break;

            // 多点松开
            case MotionEvent.ACTION_POINTER_UP:
                mode = MODE.NONE;

                break;
        }

        mGestureDetector.onTouchEvent(event);// 执行双击和长按等手势操作

        return true;
    }

    /**
     * 按下
     **/
    void onTouchDown(MotionEvent event) {
        mode = MODE.DRAG;

        current_x = (int) event.getRawX();
        current_y = (int) event.getRawY();

        start_x = (int) event.getX();
        start_y = current_y - this.getTop();

    }

    /**
     * 两个手指 只能放大缩小
     **/
    void onPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            mode = MODE.ZOOM;
            beforeLenght = getDistance(event);// 获取两点的距离
        }
    }

    /**
     * 移动的处理
     **/
    void onTouchMove(MotionEvent event) {
        int left = 0, top = 0, right = 0, bottom = 0;
        /** 处理拖动 **/
        if (mode == MODE.DRAG) {

            /** 在这里要进行判断处理，防止在drag时候越界 **/

            /** 获取相应的l，t,r ,b **/
            left = current_x - start_x;
            right = current_x + this.getWidth() - start_x;
            top = current_y - start_y;
            bottom = current_y - start_y + this.getHeight();

            this.setPosition(left, top, right, bottom);

            current_x = (int) event.getRawX();
            current_y = (int) event.getRawY();

        }
        // 处理缩放
        else if (mode == MODE.ZOOM) {

            afterLenght = getDistance(event);// 获取两点的距离

            float gapLenght = afterLenght - beforeLenght;// 变化的长度

            if (Math.abs(gapLenght) > 5f) {
                scale_temp = afterLenght / beforeLenght;// 求的缩放的比例

                this.setScale(scale_temp);

                beforeLenght = afterLenght;
            }
        }

    }

    /**
     * 获取两点的距离
     **/
    float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 实现处理拖动
     **/
    public void setPosition(int left, int top, int right, int bottom) {
        this.layout(left, top, right, bottom);
    }

    /**
     * 处理缩放
     **/
    void setScale(float scale) {

        // 控制缩放的灵敏度？
        int disX = (int) (this.getWidth() * Math.abs(1 - scale)) / 2;// 获取缩放水平距离
        int disY = (int) (this.getHeight() * Math.abs(1 - scale)) / 2;// 获取缩放垂直距离

        // 放大
        if (scale > 1 && this.getWidth() <= MAX_W) {
            current_Left = this.getLeft() - disX;
            current_Top = this.getTop() - disY;
            current_Right = this.getRight() + disX;
            current_Bottom = this.getBottom() + disY;

            this.setFrame(current_Left, current_Top, current_Right,
                    current_Bottom);
        }
        // 缩小
        else if (scale < 1 && this.getWidth() >= MIN_W) {
            current_Left = this.getLeft() + disX;
            current_Top = this.getTop() + disY;
            current_Right = this.getRight() - disX;
            current_Bottom = this.getBottom() - disY;

            this.setFrame(current_Left, current_Top, current_Right,
                    current_Bottom);
        }
    }

    /**
     * 手势监听器
     */
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        /* 双击操作 */
        @Override
        public boolean onDoubleTap(MotionEvent arg0) {
            if (Constant.isDebug)
                Toast.makeText(mContext, "双击操作", Toast.LENGTH_SHORT).show();

            return false;
        }

        /* 长按操作 */
        @Override
        public void onLongPress(MotionEvent ev) {//长按手势并不会触发ACTION_UP事件
            // 从容器中移除当前对象
            parentContainer.removeView(sonContainer);
            sonContainer.removeView(DragAndZoomImageView.this);
            // 把该灯从调出来的灯的集合里移除
            ((DiyActivity) mContext).mSelectedLightSA.remove(mLightCount);
        }
    }
}
