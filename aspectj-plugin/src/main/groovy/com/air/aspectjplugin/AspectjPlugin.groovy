package com.air.aspectjplugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class AspectjPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("test by glt")
        def hasApp = project.plugins.withType(AppPlugin)
        def hasLib = project.plugins.withType(LibraryPlugin)
        if (!hasApp && !hasLib){
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }

        final def log = project.logger
        final  def variants
        if (hasApp) {
            println("glt hasapp = true" )
            variants = project.android.applicationVariants
        }else {
            println("glt hasapp = false" )
            variants = project.android.libraryVariants
        }

        println("glt hasapp = " + hasApp)

        variants.all { variant ->
            JavaCompile javaCompile = variant.javaCompile
            javaCompile.doLast {
                String[] args = [
                        "-showWeaveInfo",
                        "-1.5",
                        "-inpath", javaCompile.destinationDir.toString(),
                        "-aspectpath", javaCompile.classpath.asPath,
                        "-d", javaCompile.destinationDir.toString(),
                        "-classpath", javaCompile.classpath.asPath,
                        "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)
                ]
                log.debug "ajc args: " + Arrays.toString(args)
                println("glt ajc args: " + Arrays.toString(args))
                MessageHandler handler = new MessageHandler(true);
                new Main().run(args, handler);
                for (IMessage message : handler.getMessages(null, true)) {
                    switch (message.getKind()) {
                        case IMessage.ABORT:
                        case IMessage.ERROR:
                        case IMessage.FAIL:
                            println("glt message  abort = " + message.message + "--" + message.thrown)
                            log.error message.message, message.thrown
                            break;
                        case IMessage.WARNING:
                            println("glt message warning = " + message.message + "--" + message.thrown)
                            log.warn message.message, message.thrown
                            break;
                        case IMessage.INFO:
                            println("glt message  info = " + message.message + "--" + message.thrown)
                            log.info message.message, message.thrown
                            break;
                        case IMessage.DEBUG:
                            println("glt message debug = " + message.message + "--" + message.thrown)
                            log.debug message.message, message.thrown
                            break;
                    }
                }
            }

        }
    }
}