package com.k1sak1.goetyawaken.utils.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Inspired by
 * <a href=
 * "https://github.com/Mega32K/ending_library/?tab=readme-ov-file">ending_library</a>
 * project.
 * Original author: MegaDarkness
 * </p>
 */
@Retention(RetentionPolicy.CLASS)
public @interface NoModDependsMixin {
    String value() default "goetyawaken";
}
