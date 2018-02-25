package com.logex.fragmentation.helper.internal;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.logex.common.R;
import com.logex.fragmentation.anim.FragmentAnimator;

/**
 * Hide
 * Created by liguangxi on 16-12-18.
 */
public final class AnimatorHelper {
    private Animation noneAnim, noneAnimFixed;
    public Animation enterAnim, exitAnim, popEnterAnim, popExitAnim;

    private Context context;
    private FragmentAnimator fragmentAnimator;

    public AnimatorHelper(Context context, FragmentAnimator fragmentAnimator) {
        this.context = context;
        this.fragmentAnimator = fragmentAnimator;
        initEnterAnim();
        initExitAnim();
        initPopEnterAnim();
        initPopExitAnim();
    }

    public Animation getNoneAnim() {
        if (noneAnim == null) {
            noneAnim = AnimationUtils.loadAnimation(context, R.anim.no_anim);
        }
        return noneAnim;
    }

    public Animation getNoneAnimFixed() {
        if (noneAnimFixed == null) {
            noneAnimFixed = new Animation() {};
        }
        return noneAnimFixed;
    }

    @Nullable
    public Animation getViewPagerChildFragmentAnimFixed(Fragment fragment, boolean enter) {
        if (!enter && fragment.getTag() != null && fragment.getTag().startsWith("android:switcher:")
                && fragment.getUserVisibleHint()) {
            Animation animation = new Animation() {
            };
            animation.setDuration(exitAnim.getDuration());
            return animation;
        }
        return null;
    }

    private Animation initEnterAnim() {
        if (fragmentAnimator.getEnter() == 0) {
            enterAnim = AnimationUtils.loadAnimation(context, R.anim.no_anim);
        } else {
            enterAnim = AnimationUtils.loadAnimation(context, fragmentAnimator.getEnter());
        }
        return enterAnim;
    }

    private Animation initExitAnim() {
        if (fragmentAnimator.getExit() == 0) {
            exitAnim = AnimationUtils.loadAnimation(context, R.anim.no_anim);
        } else {
            exitAnim = AnimationUtils.loadAnimation(context, fragmentAnimator.getExit());
        }
        return exitAnim;
    }

    private Animation initPopEnterAnim() {
        if (fragmentAnimator.getPopEnter() == 0) {
            popEnterAnim = AnimationUtils.loadAnimation(context, R.anim.no_anim);
        } else {
            popEnterAnim = AnimationUtils.loadAnimation(context, fragmentAnimator.getPopEnter());
        }
        return popEnterAnim;
    }

    private Animation initPopExitAnim() {
        if (fragmentAnimator.getPopExit() == 0) {
            // 用于解决 start新Fragment时,转场动画过程中上一个Fragment页面空白问题
            popExitAnim = AnimationUtils.loadAnimation(context, R.anim.pop_exit_no_anim);
        } else {
            popExitAnim = AnimationUtils.loadAnimation(context, fragmentAnimator.getPopExit());
        }
        return popExitAnim;
    }
}
