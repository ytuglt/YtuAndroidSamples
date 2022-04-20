package com.air.javapoetannotator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

public class FactoryCodeBuilder {

    private static final String SUFFIX = "Factory";

    private String mSupperClsName;
    private Map<String, FactoryAnnotatedCls> mAnnotatedClassed = new LinkedHashMap<>();

    public void add(FactoryAnnotatedCls annotatedCls) {
        String key = annotatedCls.getAnnotatedClsElement().getQualifiedName().toString();
        if (mAnnotatedClassed.get(key) != null) {
            return;
        }

        mAnnotatedClassed.put(key, annotatedCls);
    }

    public void clear() {
        mAnnotatedClassed.clear();
    }

    public FactoryCodeBuilder setSupperClsName(String supperClsName) {
        this.mSupperClsName = supperClsName;
        return this;
    }

    public void generateCode(Messager messager, Elements elementUtil, Filer filer) throws IOException {
        TypeElement superClassName = elementUtil.getTypeElement(mSupperClsName);
        String factoryClassName = superClassName.getSimpleName() + SUFFIX;
        messager.printMessage(Diagnostic.Kind.NOTE, "FactoryCodeBuilder generateCode111" );

        PackageElement pkg = elementUtil.getPackageOf(superClassName);

        messager.printMessage(Diagnostic.Kind.NOTE, "FactoryCodeBuilder generateCode2221" );

        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();


        messager.printMessage(Diagnostic.Kind.NOTE, "FactoryCodeBuilder generateCode3333" );

        TypeSpec typeSpec = TypeSpec.classBuilder(factoryClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(newCreateMethod(elementUtil, superClassName))
                .addMethod(newCompareIdMethod())
                .build();

        messager.printMessage(Diagnostic.Kind.NOTE, "FactoryCodeBuilder generateCode44444"  + packageName);

        JavaFile javaFile =  JavaFile.builder(packageName, typeSpec)
                .build();
        messager.printMessage(Diagnostic.Kind.NOTE, "FactoryCodeBuilder generateCode6666"  + packageName);

        javaFile.writeTo(filer);

        messager.printMessage(Diagnostic.Kind.NOTE, "FactoryCodeBuilder generateCode5555" );

    }

    private MethodSpec newCreateMethod(Elements elementUtils, TypeElement superClassName) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .addParameter(int.class, "id")
                .returns(TypeName.get(superClassName.asType()));
        method.beginControlFlow("if (id <0)")
                .addStatement("throw new IllegalArgumentException($S)","id is less then 0!")
                .endControlFlow();
        for (FactoryAnnotatedCls annotatedCls : mAnnotatedClassed.values()) {
            String packName = elementUtils.getPackageOf(annotatedCls.getAnnotatedClsElement())
                    .getQualifiedName().toString();
            String clsName = annotatedCls.getAnnotatedClsElement().getSimpleName().toString();
            ClassName cls = ClassName.get(packName, clsName);

            int[] ids = annotatedCls.getIds();
            String allId = "{";
            for (int id : ids) {
                allId = allId + (allId.equals("{") ? "": ",") + id;
            }
            allId += "}";

            method.beginControlFlow("if (compareId(new int[]$L, id))", allId)
                    .addStatement("return new $T()", cls)
                    .endControlFlow();
        }

        method.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");
        return  method.build();
    }

    private MethodSpec newCompareIdMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("compareId") //设置函数方法名字
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC) //设置方法类型为private static
                .addParameter(int[].class, "ids") //设置参数int[] ids
                .addParameter(int.class, "id") //设置参数int id
                .returns(TypeName.BOOLEAN); //设置返回类型

        builder.beginControlFlow("for (int i : ids)")
                .beginControlFlow("if (i == id)")
                .addStatement("return true")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return false");
        return builder.build();
    }

//    private MethodSpec newCompareIdMethod() {
//        MethodSpec.Builder builder = MethodSpec.methodBuilder("compareId") //设置函数方法名字
//                .addModifiers(Modifier.PRIVATE, Modifier.STATIC) //设置方法类型为private static
//                .addParameter(int[].class, "ids") //设置参数int[] ids
//                .addParameter(int.class, "id") //设置参数int id
//                .returns(TypeName.BOOLEAN); //设置返回类型
//
//        builder.beginControlFlow("for (int i : ids)") //开始一个控制流
//                .beginControlFlow("if (i == id)") //在以上for循环中加入一个if控制流
//                .addStatement("return true") //添加一行代码，最后会自动添加分号";"
//                .endControlFlow() //结束一个控制流，add和end要成对调用。这里对应if的控制流
//                .endControlFlow() //结束for控制流
//                .addStatement("return false"); //按添加返回
//
//        return builder.build();
//    }

}
