package moe.karla.asm.gradle.accesstransform;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;

public interface AccessTransformAttribute extends Named {
    Attribute<AccessTransformAttribute> ATTRIBUTE_KEY = Attribute.of("moe.karla.asm.transform.access", AccessTransformAttribute.class);

    String TRANSFORMED = "transformed";
    String UNTRANSFORMED = "untransformed";
}
