package com.liupf.plugin;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Set;

import javassist.ClassPool;

/**
 * @author Liupengfei
 * @describe TODO
 * @date on 2019/9/30 16:23
 */
public class TestTransform extends Transform {

    private static final String CLICK_LISTENER = "android.view.View\\$OnClickListener";

    ClassPool pool = ClassPool.getDefault();

    Project project;

    TestTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "ModifyTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);


    }
}
