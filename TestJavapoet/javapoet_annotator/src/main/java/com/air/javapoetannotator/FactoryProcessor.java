package com.air.javapoetannotator;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {
    private Types mTypeUtil;
    private Elements mElementUtil;
    private Filer mFiler;
    private Messager mMessager;

    private FactoryCodeBuilder mFactoryCodeBuilder = new FactoryCodeBuilder();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTypeUtil = processingEnv.getTypeUtils();
        mElementUtil = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mMessager.printMessage(Diagnostic.Kind.NOTE, "FactoryProcessor init");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "FactoryProcessor getSupportedAnnotationTypes");

        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Factory.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "FactoryProcessor getSupportedSourceVersion");

        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "FactoryProcessor process");

        String supperClsPath = "";
        mFactoryCodeBuilder.clear();
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Factory.class);
        mMessager.printMessage(Diagnostic.Kind.NOTE, "FactoryProcessor process " + elements.size());

        try {
            for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Factory.class)) {
                mMessager.printMessage(Diagnostic.Kind.NOTE, "FactoryProcessor process "  + annotatedElement.getSimpleName());

                if (annotatedElement.getKind() != ElementKind.CLASS) {
                    error(annotatedElement, String.format("Only class can be annotated with @%s",
                            Factory.class.getSimpleName()));
                }

                TypeElement typeElement = (TypeElement) annotatedElement;
                FactoryAnnotatedCls factoryAnnotatedCls = new FactoryAnnotatedCls(typeElement);
                supperClsPath = factoryAnnotatedCls.getSupperClsQualifiedName();
                checkValidClass(factoryAnnotatedCls);
                mFactoryCodeBuilder.add(factoryAnnotatedCls);
            }

            mMessager.printMessage(Diagnostic.Kind.NOTE, "FactoryProcessor process --------------111 " );


            if (supperClsPath != null && !supperClsPath.equals("")) {
                mFactoryCodeBuilder.setSupperClsName(supperClsPath)
                        .generateCode(mMessager, mElementUtil, mFiler);

                mMessager.printMessage(Diagnostic.Kind.NOTE, "FactoryProcessor process --------------222 " );

            }
        } catch (ProcessingException processingException) {
            error(processingException.getElement(), processingException.getMessage());
        } catch (IOException e) {
            error(null, e.getMessage());
        }

        return false;
    }

    private void checkValidClass(FactoryAnnotatedCls item) throws  ProcessingException{
        TypeElement classElement = item.getAnnotatedClsElement();
        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            throw new ProcessingException(classElement, "The class %s is not public",
                    classElement.getQualifiedName().toString());
        }

        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            throw new ProcessingException(classElement, "The class %s is abstract. You can't annotate abstract classes with @%",
                    classElement.getQualifiedName().toString(), Factory.class.getSimpleName());
        }

        TypeElement superClassElement = mElementUtil.getTypeElement(item.getSupperClsQualifiedName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            if (!classElement.getInterfaces().contains(superClassElement.asType())) {
                throw new ProcessingException(classElement, "The class %s annotated with @%s must implement the interface %s",
                        classElement.getQualifiedName().toString(), Factory.class.getSimpleName(), item.getSupperClsQualifiedName());
            }
        } else {
            TypeElement currentClass = classElement;
            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();

                if (superClassType.getKind() == TypeKind.NONE) {
                    throw new ProcessingException(classElement,
                            "The class %s annotated with @%s must inherit from %s",
                            classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                            item.getSupperClsQualifiedName());
                }

                if (superClassType.toString().equals(item.getSupperClsQualifiedName())) {
                    break;
                }

                currentClass = (TypeElement) mTypeUtil.asElement(superClassType);//父类
            }
        }

        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosed;
                if (constructorElement.getParameters().size() == 0 &&
                        constructorElement.getModifiers().contains(Modifier.PUBLIC)) {
                    return;
                }
            }
        }
        // No empty constructor found
        throw new ProcessingException(classElement,
                "The class %s must provide an public empty default constructor",
                classElement.getQualifiedName().toString());
    }

    public void error(Element e, String msg) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}
