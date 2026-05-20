<template>
  <div class="about-page">
    <!-- Hero -->
    <section class="about-hero">
      <div class="hero-bg-orb orb-1"></div>
      <div class="hero-bg-orb orb-2"></div>
      <h1 class="hero-title">关于 <span class="gradient-text">FitZone</span></h1>
      <p class="hero-subtitle">
        始于热爱，忠于专业 —— 打造城市中最具品质感的健身空间
      </p>
    </section>

    <!-- 介绍 -->
    <section class="about-intro">
      <div class="section-inner">
        <div class="intro-content">
          <h2 class="intro-heading">我们的<span class="gradient-text">故事</span></h2>
          <p class="intro-text">
            FitZone 成立于 2020 年，是一家致力于提供高端健身体验的综合健身中心。
            我们相信每一次挥洒汗水都应该在舒适、专业的环境中进行。从器材的选用到教练的培养，
            从课程的编排到空间的设计，我们在每一个细节中注入对健身事业的热忱。
          </p>
          <p class="intro-text">
            三年来，FitZone 已服务超过 10,000 名会员，帮助无数人实现了健康蜕变。
            我们不只是一个健身房，更是一个汇聚健身爱好者的社区，一个让你遇见更好自己的地方。
          </p>
        </div>
      </div>
    </section>

    <!-- 特色 -->
    <section class="about-features">
      <div class="section-inner">
        <h2 class="section-title">我们的<span class="gradient-text">特色</span></h2>
        <div class="features-grid">
          <div class="feature-card glass-card" v-for="f in features" :key="f.title">
            <div class="feature-icon">{{ f.icon }}</div>
            <h3 class="feature-title">{{ f.title }}</h3>
            <p class="feature-desc">{{ f.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 数据 -->
    <section class="about-stats">
      <div class="section-inner">
        <div class="stats-grid">
          <div class="stat-item" v-for="s in stats" :key="s.label">
            <div class="stat-value gradient-text">{{ s.value }}</div>
            <div class="stat-label">{{ s.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 联系我们 -->
    <section class="about-contact">
      <div class="section-inner">
        <h2 class="section-title">联系<span class="gradient-text">我们</span></h2>
        <div class="contact-grid">
          <div class="contact-card glass-card" v-for="c in contacts" :key="c.label">
            <div class="contact-icon">{{ c.icon }}</div>
            <div class="contact-label">{{ c.label }}</div>
            <div class="contact-value">{{ c.value }}</div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const features = [
  { icon: '🏋️', title: '专业设备', desc: '引进国际顶级品牌健身器材，涵盖力量训练、有氧运动、功能性训练等全方位设备。' },
  { icon: '👨‍🏫', title: '资深教练', desc: '每位教练均持有国际认证，平均从业 8 年以上，为您量身定制个性化训练计划。' },
  { icon: '📋', title: '科学方案', desc: '基于运动科学理论，结合体测数据，制定从入门到进阶的系统训练方案。' },
  { icon: '✨', title: '舒适环境', desc: '现代化的空间设计、顶级的空调新风系统、洁净的淋浴更衣设施，尽享品质体验。' }
]

const stats = [
  { value: '10,000+', label: '服务会员' },
  { value: '50+', label: '专业教练' },
  { value: '200+', label: '精品课程' },
  { value: '5,000㎡', label: '场馆面积' }
]

const contacts = [
  { icon: '📍', label: '地址', value: '北京市朝阳区建国路88号 FitZone 健身中心' },
  { icon: '📞', label: '电话', value: '400-888-9999' },
  { icon: '✉️', label: '邮箱', value: 'contact@fitzone.com' },
  { icon: '🕐', label: '营业时间', value: '周一至周日 06:00 - 23:00' }
]
</script>

<style scoped>
.about-page { min-height: 60vh; }

/* Hero */
.about-hero {
  text-align: center;
  padding: 80px 40px 60px;
  position: relative;
  overflow: hidden;
}

.hero-bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.15;
  pointer-events: none;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: var(--color-accent-cyan);
  top: -100px;
  left: -100px;
}

.orb-2 {
  width: 350px;
  height: 350px;
  background: var(--color-accent-purple);
  bottom: -80px;
  right: -80px;
}

.hero-title {
  font-size: 48px;
  font-weight: 900;
  margin-bottom: 16px;
  position: relative;
}

.hero-subtitle {
  color: var(--text-secondary);
  font-size: 18px;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.7;
  position: relative;
}

/* Section common */
.section-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 40px;
}

.section-title {
  font-size: 30px;
  font-weight: 800;
  text-align: center;
  margin-bottom: 40px;
}

/* Intro */
.about-intro {
  padding: 60px 0;
}

.intro-heading {
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 24px;
}

.intro-text {
  color: var(--text-secondary);
  font-size: 15px;
  line-height: 1.9;
  margin-bottom: 16px;
}

/* Features */
.about-features {
  padding: 60px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.feature-card {
  padding: 32px 24px;
  text-align: center;
  transition: all var(--transition-normal);
}

.feature-card:hover {
  transform: translateY(-6px);
  border-color: var(--border-glow);
  box-shadow: var(--shadow-glow);
}

.feature-icon {
  font-size: 42px;
  margin-bottom: 18px;
}

.feature-title {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.feature-desc {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.8;
}

/* Stats */
.about-stats {
  padding: 50px 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.stat-item {
  text-align: center;
  padding: 28px 16px;
}

.stat-value {
  font-size: 36px;
  font-weight: 900;
  margin-bottom: 8px;
}

.stat-label {
  color: var(--text-muted);
  font-size: 14px;
  font-weight: 500;
}

/* Contact */
.about-contact {
  padding: 60px 0 80px;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.contact-card {
  padding: 28px 20px;
  text-align: center;
}

.contact-card:hover {
  transform: translateY(-4px);
  border-color: var(--border-glow);
  box-shadow: var(--shadow-glow);
}

.contact-icon {
  font-size: 32px;
  margin-bottom: 14px;
}

.contact-label {
  color: var(--text-muted);
  font-size: 13px;
  margin-bottom: 6px;
}

.contact-value {
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 600;
}

@media (max-width: 1024px) {
  .features-grid, .stats-grid, .contact-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 640px) {
  .hero-title { font-size: 32px; }
  .features-grid, .stats-grid, .contact-grid { grid-template-columns: 1fr; }
}
</style>
