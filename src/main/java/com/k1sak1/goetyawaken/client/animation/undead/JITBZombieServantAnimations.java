package com.k1sak1.goetyawaken.client.animation.undead;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class JITBZombieServantAnimations {

        public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(0.0F)
                        .build();

        public static final AnimationDefinition SWELL = AnimationDefinition.Builder.withLength(1.0192F)
                        .addAnimation("gaizi", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(0.2398F, KeyframeAnimations.degreeVec(120.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM)))
                        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                                        new Keyframe(0.3597F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.5995F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR)))
                        .addAnimation("box", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                                        new Keyframe(0.3597F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.5995F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR)))
                        .addAnimation("box", new AnimationChannel(AnimationChannel.Targets.POSITION,
                                        new Keyframe(0.3597F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.5995F, KeyframeAnimations.posVec(0.0F, 0.0F, -3.0F),
                                                        AnimationChannel.Interpolations.LINEAR)))
                        .addAnimation("joker", new AnimationChannel(AnimationChannel.Targets.POSITION,
                                        new Keyframe(0.06F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.2398F, KeyframeAnimations.posVec(0.0F, 5.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR)))
                        .build();
        public static final AnimationDefinition MUSIC = AnimationDefinition.Builder.withLength(1.1299F).looping()
                        .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.113F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(0.226F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(0.339F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(0.452F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(0.565F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(0.678F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(0.791F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(0.904F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(1.017F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(1.1299F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM)))
                        .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.113F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.226F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.339F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.452F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.565F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.678F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.791F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.904F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(1.017F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(1.1299F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR)))
                        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                                        new Keyframe(0.3955F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.565F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR),
                                        new Keyframe(0.7439F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.LINEAR)))
                        .build();

        public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(1.1189F).looping()
                        .addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(0.5594F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(1.1189F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM)))
                        .addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(0.5594F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM),
                                        new Keyframe(1.1189F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F),
                                                        AnimationChannel.Interpolations.CATMULLROM)))
                        .build();
}