package com.air.javapoetannotator;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

public class FactoryAnnotatedCls {

    private TypeElement mAnnotatedClsElement;

    private String mSupperClsQualifiedName;

    private String mSupperClsSampleName;

    private int[] mIds;

    public FactoryAnnotatedCls(TypeElement classElement) throws ProcessingException {
        this.mAnnotatedClsElement = classElement;
        Factory annotation = classElement.getAnnotation(Factory.class);
        mIds = annotation.ids();
        try {
            mSupperClsSampleName = annotation.superClass().getSimpleName();
            mSupperClsQualifiedName = annotation.superClass().getCanonicalName();
        } catch (MirroredTypeException mirroredTypeException) {
            DeclaredType classTypeMirror = (DeclaredType) mirroredTypeException.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            mSupperClsQualifiedName = classTypeElement.getQualifiedName().toString();
            mSupperClsSampleName = classTypeElement.getSimpleName().toString();
        }

        if (mIds == null || mIds.length == 0) {
            throw new ProcessingException(classElement, "id() in @%s for class %s is null or empty! "
                    + "that's not allowed",
                    Factory.class.getSimpleName(),
                    classElement.getQualifiedName().toString());
        }

        if (mSupperClsSampleName == null || mSupperClsQualifiedName == null) {
            throw new ProcessingException(classElement, "superClass() in @%s for class is null or" +
                    " empty! that's not allowed",
                    Factory.class.getSimpleName(),
                    classElement.getQualifiedName().toString());
        }
    }

    public TypeElement getAnnotatedClsElement() {
        return mAnnotatedClsElement;
    }

    public String getSupperClsQualifiedName() {
        return mSupperClsQualifiedName;
    }

    public String getSupperClsSampleName() {
        return mSupperClsSampleName;
    }

    public int[] getIds() {
        return mIds;
    }
}
