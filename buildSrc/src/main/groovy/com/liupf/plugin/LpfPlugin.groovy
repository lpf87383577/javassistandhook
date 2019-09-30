package com.liupf.plugin

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class LpfPlugin implements Plugin<Project>{

    @Override
    void apply(Project target) {

        def extension = target.extensions.create('hencoder2',ExtensionDemo)

        target.afterEvaluate {
            println "hellow2 ${extension.name}"
        }

    }

}