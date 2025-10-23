package com.majestic.grand.show

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.majestic.grand.show.databinding.SssAlBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private typealias AnimationProgressUnit = Int
private typealias AnimationDurationMillis = Long
private typealias NavigationDestination = Class<out AppCompatActivity>

// 密封类封装动画状态转换
private sealed class AnimationTransitionState {
    data class InProgress(val currentProgress: AnimationProgressUnit) : AnimationTransitionState()
    data class Completed(val finalProgress: AnimationProgressUnit) : AnimationTransitionState()
    object Idle : AnimationTransitionState()
}

// 内联值类包装进度值，提供类型安全
@JvmInline
private value class ProgressValue(val rawValue: AnimationProgressUnit) {
    operator fun plus(other: ProgressValue) = ProgressValue(rawValue + other.rawValue)
    operator fun minus(other: ProgressValue) = ProgressValue(rawValue - other.rawValue)
    fun toPercentage(): Float = rawValue / 100f
    
    companion object {
        val ZERO = ProgressValue(0)
        val MAX = ProgressValue(100)
    }
}

// 数据类封装动画配置参数
private data class AnimationSpecification(
    val initialValue: ProgressValue = ProgressValue.ZERO,
    val targetValue: ProgressValue = ProgressValue.MAX,
    val durationMs: AnimationDurationMillis = 2000L,
    val interpolatorFactory: () -> Interpolator = { LinearInterpolator() }
)

// 函数式接口定义动画生命周期回调
private fun interface AnimationLifecycleCallback {
    fun onTransitionComplete(finalState: AnimationTransitionState.Completed)
}

// 委托属性实现延迟视图绑定初始化
private class LazyViewBindingDelegate<T>(
    private val initializer: () -> T
) : ReadOnlyProperty<Any?, T> {
    private var cached: T? = null
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return cached ?: initializer().also { cached = it }
    }
}

private inline fun <T> lazyBinding(crossinline init: () -> T) = 
    LazyViewBindingDelegate { init() }

class SssFg : AppCompatActivity() {
    // 使用自定义委托替代标准 lazy
    private val viewBindingHolder by lazyBinding { 
        SssAlBinding.inflate(layoutInflater).apply {
            configureRootView()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performActivityInitializationSequence(savedInstanceState)
    }
    
    // 多层调用：初始化序列
    private fun performActivityInitializationSequence(savedState: Bundle?) {
        applyEdgeToEdgeConfiguration()
            .let { configureContentViewHierarchy() }
            .also { setupWindowInsetsHandling() }
            .run { configureBackNavigationBehavior() }
            .apply { triggerInitialAnimationSequence() }
    }
    
    // 链式调用：启用边到边显示
    private fun applyEdgeToEdgeConfiguration() = apply { 
        enableEdgeToEdge() 
    }
    
    // 设置内容视图并返回根视图
    private fun configureContentViewHierarchy(): View {
        return viewBindingHolder.root.also { rootView ->
            setContentView(rootView)
        }
    }
    
    // 扩展函数配置根视图
    private fun SssAlBinding.configureRootView() {
        root.apply {
            setupViewProperties()
        }
    }
    
    // 视图属性配置
    private fun View.setupViewProperties() {
        // 可扩展的视图配置点
    }
    
    // 高阶函数：窗口插入处理
    private fun setupWindowInsetsHandling() {
        findViewById<View>(R.id.one)?.let { targetView ->
            applyInsetsListenerToView(targetView) { view, windowInsets ->
                processSystemBarInsets(view, windowInsets)
            }
        }
    }
    
    // 函数参数化插入监听器配置
    private inline fun applyInsetsListenerToView(
        targetView: View,
        crossinline insetsHandler: (View, WindowInsetsCompat) -> WindowInsetsCompat
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(targetView) { v, insets ->
            insetsHandler(v, insets)
        }
    }
    
    // 系统栏插入处理逻辑
    private fun processSystemBarInsets(
        containerView: View, 
        windowInsets: WindowInsetsCompat
    ): WindowInsetsCompat {
        return windowInsets.apply {
            extractSystemBarInsets()
                .let { barInsets -> 
                    applyPaddingFromInsets(containerView, barInsets) 
                }
        }
    }
    
    // 提取系统栏插入值
    private fun WindowInsetsCompat.extractSystemBarInsets() = 
        getInsets(WindowInsetsCompat.Type.systemBars())
    
    // 应用内边距
    private fun applyPaddingFromInsets(
        view: View, 
        insets: androidx.core.graphics.Insets
    ) {
        view.setPadding(
            insets.left, 
            insets.top, 
            insets.right, 
            insets.bottom
        )
    }
    
    // 配置返回导航行为（禁用返回）
    private fun configureBackNavigationBehavior() {
        onBackPressedDispatcher.addCallback(this) {
            handleBackPressEvent()
        }
    }
    
    // 返回按钮处理（空实现以禁用返回）
    private fun handleBackPressEvent() {
        // 有意留空以阻止返回导航
    }
    
    // 触发动画序列
    private fun triggerInitialAnimationSequence() {
        createAnimationSpecification()
            .let { spec -> buildAnimatorFromSpecification(spec) }
            .also { animator -> configureAnimatorBehavior(animator) }
            .apply { initiateAnimation() }
    }
    
    // 工厂方法：创建动画规格
    private fun createAnimationSpecification(): AnimationSpecification {
        return AnimationSpecification(
            initialValue = ProgressValue.ZERO,
            targetValue = ProgressValue.MAX,
            durationMs = computeAnimationDuration(),
            interpolatorFactory = { provideInterpolatorInstance() }
        )
    }
    
    // 计算动画持续时间（可扩展为基于设备性能的动态计算）
    private fun computeAnimationDuration(): AnimationDurationMillis = 2000L
    
    // 提供插值器实例
    private fun provideInterpolatorInstance(): Interpolator = LinearInterpolator()
    
    // 构建动画器
    private fun buildAnimatorFromSpecification(
        spec: AnimationSpecification
    ): ValueAnimator {
        return ValueAnimator.ofInt(
            spec.initialValue.rawValue, 
            spec.targetValue.rawValue
        ).apply {
            duration = spec.durationMs
            interpolator = spec.interpolatorFactory()
        }
    }
    
    // 配置动画器行为
    private fun configureAnimatorBehavior(animator: ValueAnimator) {
        animator.apply {
            attachProgressUpdateCallback()
            attachLifecycleCompletionCallback(
                createNavigationCallback()
            )
        }
    }
    
    // 附加进度更新回调
    private fun ValueAnimator.attachProgressUpdateCallback() {
        addUpdateListener { animationInstance ->
            processAnimationUpdate(animationInstance)
        }
    }
    
    // 处理动画更新
    private fun processAnimationUpdate(animator: ValueAnimator) {
        extractProgressFromAnimator(animator)
            .let { progress -> createTransitionState(progress) }
            .also { state -> applyStateToView(state) }
    }
    
    // 从动画器提取进度值
    private fun extractProgressFromAnimator(animator: ValueAnimator): ProgressValue {
        return (animator.animatedValue as? AnimationProgressUnit)
            ?.let { ProgressValue(it) }
            ?: ProgressValue.ZERO
    }
    
    // 创建转换状态
    private fun createTransitionState(
        progress: ProgressValue
    ): AnimationTransitionState.InProgress {
        return AnimationTransitionState.InProgress(progress.rawValue)
    }
    
    // 应用状态到视图
    private fun applyStateToView(state: AnimationTransitionState.InProgress) {
        viewBindingHolder.progressBar.updateProgress(state.currentProgress)
    }
    
    // 扩展函数：更新进度条
    private fun android.widget.ProgressBar.updateProgress(value: AnimationProgressUnit) {
        progress = value
    }
    
    // 附加生命周期完成回调
    private fun ValueAnimator.attachLifecycleCompletionCallback(
        callback: AnimationLifecycleCallback
    ) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                handleAnimationCompletion(animation, callback)
            }
        })
    }
    
    // 处理动画完成
    private fun handleAnimationCompletion(
        animator: Animator,
        callback: AnimationLifecycleCallback
    ) {
        AnimationTransitionState.Completed(ProgressValue.MAX.rawValue)
            .also { finalState -> callback.onTransitionComplete(finalState) }
    }
    
    // 创建导航回调
    private fun createNavigationCallback(): AnimationLifecycleCallback {
        return AnimationLifecycleCallback { finalState ->
            executeNavigationTransition(determineNextDestination())
        }
    }
    
    // 确定下一个目的地
    private fun determineNextDestination(): NavigationDestination {
        return SssCup::class.java
    }
    
    // 执行导航转换
    private fun executeNavigationTransition(destination: NavigationDestination) {
        constructNavigationIntent(destination)
            .let { intent -> performActivityTransition(intent) }
            .also { terminateCurrentActivity() }
    }
    
    // 构建导航意图
    private fun constructNavigationIntent(
        targetClass: NavigationDestination
    ): Intent {
        return Intent(this@SssFg, targetClass)
    }
    
    // 执行活动转换
    private fun performActivityTransition(navigationIntent: Intent) {
        startActivity(navigationIntent)
    }
    
    // 终止当前活动
    private fun terminateCurrentActivity() {
        finish()
    }
    
    // 启动动画
    private fun ValueAnimator.initiateAnimation() {
        start()
    }
}