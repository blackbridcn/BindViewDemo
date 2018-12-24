package org.com.bindviewcompiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.com.bindviewannotion.BindView;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.com.bindviewannotion.internal.Constants.NO_RES_ID;

//@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Filer filer;
    private Types typeUtils;

    private static final List<Class<? extends Annotation>> LISTENERS = Arrays.asList(//
            OnCheckedChanged.class, //
            OnClick.class, //
            OnEditorAction.class, //
            OnFocusChange.class, //
            OnItemClick.class, //
            OnItemLongClick.class, //
            OnItemSelected.class, //
            OnLongClick.class, //
            OnPageChange.class, //
            OnTextChanged.class, //
            OnTouch.class //
    );

    static final Id NO_ID = new Id(NO_RES_ID);

    /**
     * 初始化 init
     * <p>
     * <p>
     * <p>
     * public interface ProcessingEnvironment {
     * <p>
     * //返回用来在元素上进行操作的某些实用工具方法的实现。<br>
     * //  Elements是一个工具类，可以处理相关Element（包括ExecutableElement, PackageElement,
     * //                            TypeElement, TypeParameterElement, VariableElement）
     * Elements getElementUtils();
     * <p>
     * // 返回用来报告错误、警报和其他通知的 Messager。
     * Messager getMessager();
     * <p>
     * //  用来创建新源、类或辅助文件的 Filer。
     * Filer getFiler();
     * <p>
     * //  返回用来在类型上进行操作的某些实用工具方法的实现。
     * Types getTypeUtils();
     * <p>
     * // 返回任何生成的源和类文件应该符合的源版本。
     * SourceVersion getSourceVersion();
     * <p>
     * // 返回当前语言环境；如果没有有效的语言环境，则返回 null。
     * Locale getLocale();
     * <p>
     * // 返回传递给注释处理工具的特定于 processor 的选项
     * Map<String, String> getOptions();
     * <p>
     * }
     *
     * @param processingEnvironment 源码 <p>
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    //返回版本 或者是注解@SupportedSourceVersion(SourceVersion.RELEASE_8)
    @Override
    public SourceVersion getSupportedSourceVersion() {
        //java 最高版本，最低要求是1.7
        return SourceVersion.latestSupported();//super.getSupportedSourceVersion();
    }

    /**
     * 返回此Porcessor可以处理的注解操作
     *
     * @return
     */
    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }


    /**
     * 返回需要处理的注解类型:告诉编译器我处理的那些注解
     * <p>
     * 返回此 Processor 支持的注释类型的名称。结果元素可能是某一受支持注释类型的规范（完全限定）名称。
     * 它也可能是 " name.*" 形式的名称，表示所有以 " name." 开头的规范名称的注释类型集合。最后，
     * "*" 自身表示所有注释类型的集合，包括空集。注意，Processor 不应声明 "*"，
     * 除非它实际处理了所有文件；声明不必要的注释可能导致在某些环境中的性能下降。
     *
     * @return
     */
  //  @SupportedAnnotationTypes({"linjw.demo.injector.InjectView"})
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> type = new LinkedHashSet<>();
        for (Class<? extends Annotation> clazz : getSupportedAnnotation()) {
            type.add(clazz.getCanonicalName());
            System.out.print("process ------------------>"+clazz.getCanonicalName());
        }
        return type;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotation() {
        Set<Class<? extends Annotation>> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add(BindView.class);
        return linkedHashSet;
    }

    /**
     * 注解处理器的核心方法，处理具体的注解
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 通过roundEnvironment扫描所有的类文件，获取所有存在指定注解的字段
       Map<TypeElement, List<FieldViewBinding>> targetMap = getTargetMap(roundEnvironment);

        //createJavaFile(targetMap.entrySet());

        System.out.print("process ------------------>");
        System.out.print("process ------------------>");
        System.out.print("process ------------------>");

        return false;
    }

    /**
     * 获取所有存在注解的类
     *
     * @param roundEnvironment
     * @return
     */
    private Map<TypeElement, List<FieldViewBinding>> getTargetMap(RoundEnvironment roundEnvironment) {
        /**
         * 键：TypeElement，指定Activity；
         * 值：List<FieldViewBinding>，activiyt中所有的注解修饰的字段
         */
        Map<TypeElement, List<FieldViewBinding>> targetMap = new HashMap<>();

        // 1、获取代码中所有使用@BindView注解修饰的字段
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : annotatedElements) {
            // 获取字段名称 (textView)
            String fieldName = element.getSimpleName().toString();
            // 获取字段类型 (android.widget.TextView)
            TypeMirror fieldType = element.asType();
            // 获取注解元素的值 (R.id.textView)
            int viewId = element.getAnnotation(BindView.class).value();

            // 获取声明element的全限定类名 (com.zhangke.simplifybutterknife.MainActivity)
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            List<FieldViewBinding> list = targetMap.get(typeElement);
            if (list == null) {
                list = new ArrayList<>();
                targetMap.put(typeElement, list);
            }

           //list.add(new FieldViewBinding(fieldName, fieldType, viewId));

        }

        return targetMap;
    }

    /**
     * 创建Java文件
     *
     * @param entries
     */
    private void createJavaFile(Set<Map.Entry<TypeElement, List<FieldViewBinding>>> entries) {
        for (Map.Entry<TypeElement, List<FieldViewBinding>> entry : entries) {
            TypeElement typeElement = entry.getKey();
            List<FieldViewBinding> list = entry.getValue();
            if (list == null || list.size() == 0) {
                continue;
            }


            // 获取包名
            String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
            // 根据旧Java类名创建新的Java文件
            String className = typeElement.getQualifiedName().toString().substring(packageName.length() + 1);
            String newClassName = className + "_ViewBinding";


            MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.bestGuess(className), "target");
            for (FieldViewBinding fieldViewBinding : list) {
             /*   String packageNameString = fieldViewBinding.getFieldType().toString();
                ClassName viewClass = ClassName.bestGuess(packageNameString);
                methodBuilder.addStatement
                        ("target.$L=($T)target.findViewById($L)", fieldViewBinding.getFieldName()
                                , viewClass, fieldViewBinding.getViewId());*/
            }


            TypeSpec typeBuilder = TypeSpec.classBuilder(newClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodBuilder.build())
                    .build();


            JavaFile javaFile = JavaFile.builder(packageName, typeBuilder)
                    .addFileComment("Generated code from Butter Knife. Do not modify!")
                    .build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
