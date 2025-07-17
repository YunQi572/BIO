<template>
  <div class="home">
    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-content fade-in">
        <h1 class="slide-up">增强型全球生物多样性AI守护者</h1>
        <p class="slide-up delay-1">利用人工智能技术保护地球生物多样性</p>
        <div class="hero-buttons slide-up delay-2">
          <router-link to="/project-overview" class="btn btn-primary hover-lift">了解更多</router-link>
          <router-link to="/technique" class="btn btn-secondary hover-lift">探索技术</router-link>
          <router-link to="/tech-demo" class="btn btn-secondary hover-lift">技术演示</router-link>
          <router-link to="/map" class="btn btn-secondary hover-lift">生态地图</router-link>
        </div>
      </div>
    </section>

    <!-- 项目简介部分 -->
    <section class="intro-section">
      <div class="container">
        <div class="section-header fade-in-up">
          <h2>项目简介</h2>
          <div class="subtitle">智能科技助力生物多样性保护</div>
        </div>
        <div class="intro-text fade-in-up delay-1">
          <div class="background-box">
            <h3><span class="section-icon">🌍</span> 项目背景</h3>
            <p>随着全球气候变化加剧和人类活动范围扩大，地球生物多样性面临空前挑战。据联合国生物多样性公约组织数据显示，全球物种灭绝速率已达自然水平的100-1000倍，超过100万个物种濒临灭绝。传统的生物多样性监测方法存在覆盖范围有限、数据收集不连续、人力成本高昂等问题，难以应对全球性的生态危机。</p>
            <p>在此背景下，"增强型全球生物多样性AI守护者"项目应运而生，旨在利用人工智能技术突破传统监测技术的局限，构建全天候、高精度、低成本的生态监测和保护网络，为全球生物多样性保护提供强有力的技术支持和决策依据。</p>
          </div>
          
          <div class="mission-box">
            <h3><span class="section-icon">🚀</span> 项目前景</h3>
            <p>增强型全球生物多样性AI守护者是一个结合人工智能与生态保护的创新项目，旨在通过先进技术手段监测、分析和保护全球生物多样性。面对生物多样性保护领域的关键挑战，我们开发了一系列创新解决方案，构建了一个全面的生态监测和保护网络，致力于实现以下目标：</p>
            <ul class="mission-list">
              <li><span class="item-icon">🌐</span>建立全球范围内的AI生物多样性监测网络</li>
              <li><span class="item-icon">🔍</span>提供高精度物种识别和行为分析技术</li>
              <li><span class="item-icon">🔄</span>实现全天候、多模态的生态环境监测</li>
              <li><span class="item-icon">🤝</span>打造开放共享的全球生态数据协作平台</li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <!-- 四大模块：动机-挑战-创新方法 -->
    <section class="challenges-section">
      <div class="container">
        <div class="section-header fade-in-up">
          <h2><span class="section-icon">💡</span> 关键挑战与解决方案</h2>
          <div class="subtitle">前沿AI技术破解生物多样性保护难题</div>
        </div>

        <!-- 模块1 -->
        <div class="challenge-module fade-in-up">
          <div class="challenge-content">
            <div class="challenge-icon">🔍</div>
            <div class="challenge-text">
              <h3>精准识别 · 高效监测</h3>
              <div class="challenge-grid">
                <div class="challenge-item">
                  <h4><span class="mini-icon">💭</span> 动机</h4>
                  <p>个体易份标注成本高，大规模生态监测面临精确识别难题</p>
                </div>
                <div class="challenge-item challenge-highlight">
                  <h4><span class="mini-icon">⚠️</span> 挑战</h4>
                  <p>通用物种身份识别难度大，复杂自然环境下准确率低，难以大规模应用</p>
                </div>
                <div class="challenge-item">
                  <h4><span class="mini-icon">✨</span> 创新方法</h4>
                  <p>高频自适应与感知辅助通用动物身份识别算法，实现低成本、高精度的生物个体识别</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 模块2 -->
        <div class="challenge-module fade-in-up">
          <div class="challenge-content">
            <div class="challenge-icon">🔦</div>
            <div class="challenge-text">
              <h3>全天候 · 多模态感知</h3>
              <div class="challenge-grid">
                <div class="challenge-item">
                  <h4><span class="mini-icon">💭</span> 动机</h4>
                  <p>野生动物活动不分昼夜，单一监测模式难以全面捕捉生态信息</p>
                </div>
                <div class="challenge-item challenge-highlight">
                  <h4><span class="mini-icon">⚠️</span> 挑战</h4>
                  <p>通过跨模态信息实现全天候动物身份持续识别与监控难，数据整合困难</p>
                </div>
                <div class="challenge-item">
                  <h4><span class="mini-icon">✨</span> 创新方法</h4>
                  <p>个体级多模态动物识别大模型，融合视觉、声音、热成像等多源数据，实现24小时不间断监测</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 模块3 -->
        <div class="challenge-module fade-in-up">
          <div class="challenge-content">
            <div class="challenge-icon">🌙</div>
            <div class="challenge-text">
              <h3>极端环境 · 稳定识别</h3>
              <div class="challenge-grid">
                <div class="challenge-item">
                  <h4><span class="mini-icon">💭</span> 动机</h4>
                  <p>生物多样性监测往往需要在恶劣自然条件下进行，传统技术难以适应</p>
                </div>
                <div class="challenge-item challenge-highlight">
                  <h4><span class="mini-icon">⚠️</span> 挑战</h4>
                  <p>复杂极端场景，如弱光、低清晰度、极端视角等，常规识别方法难以有效处理</p>
                </div>
                <div class="challenge-item">
                  <h4><span class="mini-icon">✨</span> 创新方法</h4>
                  <p>面向弱光与极端视角的NightReid及旋转不变算法，突破传统视觉技术局限，确保识别稳定性</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 模块4 -->
        <div class="challenge-module fade-in-up">
          <div class="challenge-content">
            <div class="challenge-icon">🚨</div>
            <div class="challenge-text">
              <h3>实时分析 · 专业预警</h3>
              <div class="challenge-grid">
                <div class="challenge-item">
                  <h4><span class="mini-icon">💭</span> 动机</h4>
                  <p>生态保护需要快速响应，传统人工分析无法满足及时性需求</p>
                </div>
                <div class="challenge-item challenge-highlight">
                  <h4><span class="mini-icon">⚠️</span> 挑战</h4>
                  <p>动态信息难及时专业分析，海量数据下如何实现主动预警并挖掘价值</p>
                </div>
                <div class="challenge-item">
                  <h4><span class="mini-icon">✨</span> 创新方法</h4>
                  <p>基于微调MLLM的泛化技术实现专家级预警系统，自动分析行为异常、健康状况，提供决策支持</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 创新平台部分 -->
    <section class="platform-section fade-in-up">
      <div class="container">
        <div class="section-header">
          <h2><span class="section-icon">🌐</span> 生物多样性智能守护与生态智慧平台</h2>
          <div class="subtitle">集成前沿技术，打造全球生态保护智能网络</div>
        </div>
        <div class="platform-content">
          <p>通过整合上述创新技术，我们构建了一个全方位的生物多样性保护平台，实现从监测、分析到保护的完整闭环。该平台连接全球研究机构、保护组织和志愿者，形成协同保护网络，为全球生物多样性保护提供强大的技术支撑。</p>
          <div class="feature-highlights">
            <div class="feature float-animation">
              <div class="feature-icon">🌍</div>
              <div class="feature-text">全球覆盖</div>
            </div>
            <div class="feature float-animation delay-1">
              <div class="feature-icon">🔍</div>
              <div class="feature-text">智能监测</div>
            </div>
            <div class="feature float-animation delay-2">
              <div class="feature-icon">🤖</div>
              <div class="feature-text">专业分析</div>
            </div>
            <div class="feature float-animation delay-3">
              <div class="feature-icon">🔄</div>
              <div class="feature-text">协同保护</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 核心内容卡片 -->
    <section class="cards-section">
      <div class="container">
        <div class="section-header fade-in-up">
          <h2><span class="section-icon">🔎</span> 探索更多</h2>
          <div class="subtitle">了解我们的技术与项目</div>
        </div>
        <div class="cards-grid">
          <router-link to="/project-overview" class="card hover-lift-card">
            <div class="card-image">
              <img src="@/assets/Picture/核心技术/7094d48f-df6f-4b19-aedf-72b46c76d4ad.jpeg" alt="核心技术" class="hover-zoom">
            </div>
            <div class="card-content">
              <h3>项目概览</h3>
              <p>了解项目背景、目标和全球影响</p>
            </div>
          </router-link>

          <router-link to="/technique" class="card hover-lift-card">
            <div class="card-image">
              <img src="@/assets/Picture/项目概览/c14c8985-845c-4f71-ab76-bb94e99a720b.jpeg" alt="项目概览" class="hover-zoom">
            </div>
            <div class="card-content">
              <h3>核心技术</h3>
              <p>探索支持项目的前沿AI技术</p>
            </div>
          </router-link>

          <router-link to="/tech-demo" class="card hover-lift-card">
            <div class="card-image">
              <img src="@/assets/Picture/核心技术/acbdffbfefbedHraplgZFcBCOqLjRqNK.jpeg" alt="技术演示" class="hover-zoom">
            </div>
            <div class="card-content">
              <h3>技术演示</h3>
              <p>查看我们各项核心技术的交互式演示</p>
            </div>
          </router-link>

          <router-link to="/technique" class="card hover-lift-card">
            <div class="card-image">
              <img src="@/assets/Picture/增强型全球生物多样性AI守护者/photo-1458724338480-79bc7a8352e4.webp" alt="AI守护者" class="hover-zoom">
            </div>
            <div class="card-content">
              <h3>AI守护者</h3>
              <p>探索全球生物多样性AI守护系统</p>
            </div>
          </router-link>

          <router-link to="/map" class="card hover-lift-card">
            <div class="card-image">
              <img src="@/assets/Picture/增强型全球生物多样性AI守护者/北极生态图片.png" alt="生态地图" class="hover-zoom">
            </div>
            <div class="card-content">
              <h3>生态地图</h3>
              <p>查看全球生物多样性分布情况</p>
            </div>
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'HomeView',
  mounted() {
    this.initAnimations();
  },
  methods: {
    initAnimations() {
      // 观察元素进入视窗
      const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add('visible');
          }
        });
      }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
      });

      // 观察所有动画元素
      document.querySelectorAll('.fade-in-up, .float-animation').forEach(el => {
        observer.observe(el);
      });
    }
  }
}
</script>

<style scoped>
.home {
  width: 100%;
}

/* 动画关键帧 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
}

/* 动画类 */
.fade-in {
  animation: fadeIn 0.8s ease-out;
}

.slide-up {
  opacity: 0;
  animation: slideUp 0.8s ease-out forwards;
}

.slide-up.delay-1 {
  animation-delay: 0.2s;
}

.slide-up.delay-2 {
  animation-delay: 0.4s;
}

.fade-in-up {
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.6s ease-out;
}

.fade-in-up.visible {
  opacity: 1;
  transform: translateY(0);
}

.fade-in-up.delay-1 {
  transition-delay: 0.2s;
}

.fade-in-up.delay-2 {
  transition-delay: 0.4s;
}

.float-animation {
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease-out;
  animation: float 3s ease-in-out infinite;
  animation-play-state: paused;
}

.float-animation.visible {
  opacity: 1;
  transform: translateY(0);
  animation-play-state: running;
}

.float-animation.delay-1 {
  animation-delay: 0.5s;
}

.float-animation.delay-2 {
  animation-delay: 1s;
}

.float-animation.delay-3 {
  animation-delay: 1.5s;
}

/* 悬停效果 */
.hover-lift {
  transition: all 0.3s ease;
}

.hover-lift:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
}

.hover-lift-card {
  transition: all 0.3s ease;
}

.hover-lift-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0,0,0,0.15);
}

.hover-scale {
  transition: transform 0.3s ease;
}

.hover-scale:hover {
  transform: scale(1.05);
}

.hover-zoom {
  transition: transform 0.3s ease;
}

.card:hover .hover-zoom {
  transform: scale(1.1);
}

/* 英雄区域 */
.hero-section {
  height: 80vh;
  background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.6)), 
              url('@/assets/Picture/项目概览/f89e8c3f-c8ef-4e3c-9d05-500fde5f8f62.jpeg') no-repeat center center;
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  color: white;
  padding: 0 20px;
}

.hero-content {
  max-width: 800px;
}

.hero-content h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.hero-content p {
  font-size: 1.5rem;
  margin-bottom: 2rem;
}

.hero-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.btn {
  padding: 12px 28px;
  border-radius: 4px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.btn-primary {
  background-color: #42b983;
  color: white;
}

.btn-primary:hover {
  background-color: #3aa876;
}

.btn-secondary {
  background-color: transparent;
  color: white;
  border: 2px solid white;
}

.btn-secondary:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* 容器 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 章节通用样式 */
section {
  padding: 60px 0;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-header h2 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 1.2rem;
  color: #666;
}

/* 简介部分 */
.intro-section {
  background-color: #f9f9f9;
}

.intro-content {
  display: flex;
  align-items: center;
  gap: 40px;
}

.intro-text {
  flex: 1;
}

.intro-text p {
  margin-bottom: 1rem;
  line-height: 1.6;
}

.intro-image {
  flex: 1;
}

.intro-image img {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.feature-highlights {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
}

.feature {
  text-align: center;
}

.feature-icon {
  font-size: 2rem;
  margin-bottom: 8px;
}

/* 卡片部分 */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
  text-decoration: none;
  color: inherit;
}

.card-image {
  height: 200px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.card-content {
  padding: 20px;
}

.card-content h3 {
  margin-bottom: 10px;
  color: #2c3e50;
}

.card-content p {
  color: #666;
}

/* 新增挑战模块样式 */
.challenges-section {
  background-color: #f5f7fa;
  padding: 80px 0;
}

.challenge-module {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.08);
  margin-bottom: 30px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.challenge-module:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0,0,0,0.15);
}

.challenge-content {
  display: flex;
  padding: 30px;
}

.challenge-icon {
  font-size: 3rem;
  margin-right: 20px;
  display: flex;
  align-items: center;
}

.challenge-text {
  flex: 1;
}

.challenge-text h3 {
  color: #2c3e50;
  font-size: 1.8rem;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #42b983;
}

.challenge-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.challenge-item {
  padding: 15px;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.challenge-highlight {
  background-color: #eefaf4;
  border-left: 3px solid #42b983;
}

.challenge-item h4 {
  color: #42b983;
  margin-bottom: 10px;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
}

.challenge-item p {
  color: #555;
  line-height: 1.5;
}

/* 平台部分样式 */
.platform-section {
  background-color: #fff;
  padding: 60px 0;
}

.platform-content {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.platform-content p {
  margin-bottom: 30px;
  line-height: 1.6;
  font-size: 1.1rem;
}

/* 项目背景和使命样式 */
.background-box, .mission-box {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.background-box:hover, .mission-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.background-box h3, .mission-box h3 {
  color: #42b983;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
  font-size: 1.4rem;
}

.mission-list {
  margin-top: 15px;
  padding-left: 20px;
}

.mission-list li {
  margin-bottom: 12px;
  position: relative;
  padding-left: 40px;
  line-height: 1.6;
  display: flex;
  align-items: center;
}

.mission-list li:before {
  display: none;
}

.item-icon {
  position: absolute;
  left: 0;
  font-size: 1.5rem;
  margin-right: 15px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
}

.section-icon {
  margin-right: 10px;
  font-size: 1.3rem;
  display: inline-block;
  animation: pulse 2s infinite;
}

.intro-text p {
  line-height: 1.7;
  margin-bottom: 15px;
  color: #4a4a4a;
}

/* 图标动画效果 */
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 2.5rem;
  }
  
  .hero-content p {
    font-size: 1.2rem;
  }
  
  .intro-content {
    flex-direction: column;
  }
  
  .feature-highlights {
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .feature {
    width: 45%;
  }
  
  .background-box, .mission-box {
    padding: 20px 15px;
  }
  
  .mission-list li {
    padding-left: 12px;
  }
}

@media (max-width: 480px) {
  .hero-content h1 {
    font-size: 2rem;
  }
  
  .hero-buttons {
    flex-direction: column;
  }
}

@media (max-width: 992px) {
  .challenge-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .challenge-content {
    flex-direction: column;
  }
  
  .challenge-icon {
    margin-bottom: 15px;
    margin-right: 0;
  }
}

.mini-icon {
  margin-right: 5px;
  font-size: 1rem;
}
</style>
