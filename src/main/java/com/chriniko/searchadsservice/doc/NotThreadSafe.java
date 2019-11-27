package com.chriniko.searchadsservice.doc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE) // Note: serves only as a live code documentation.
public @interface NotThreadSafe {
}
