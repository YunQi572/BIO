<template>
  <div class="ai-root-light">
    <!-- 页面标题和描述-->
    <div v-if="currentStep === 'select'" class="page-header">
      <h1 class="main-title">🔬 AI-REID 多模态个体识别引擎</h1>
      <p class="main-subtitle">融合图像、文本与环境元数据，驱动跨场景高鲁棒性个体识别引擎，解锁前所未有的识别精度与深度洞察。</p>
    </div>

    <!-- 步骤1：选择动物样本或上传图片-->
    <transition name="fade">
      <div v-if="currentStep === 'select'" class="select-upload-wrap">
        <div class="progress-indicator">
          <div class="step-item active">
            <div class="step-circle">1</div>
            <span>选择样本</span>
          </div>
          <div class="step-line"></div>
          <div class="step-item">
            <div class="step-circle">2</div>
            <span>REID分析</span>
          </div>
          <div class="step-line"></div>
          <div class="step-item">
            <div class="step-circle">3</div>
            <span>个体档案</span>
          </div>
        </div>

        <div class="subtitle">请选择一个动物样本或上传多媒体文件进行REID个体识别（可选择多种文件类型，至少上传一个）</div>
        <div class="card-center-flex">
          <div class="card-list">
            <div v-for="(item, idx) in samples" :key="item.id" class="card-item"
              :class="{
                selected: selectedAnimalIndex === idx && !uploadedImage, 
                'card-fade-in': true,
                'custom-upload-card': item.isCustomUpload,
                'uploading': item.isCustomUpload && customUpload.isUploading
              }"
              :style="{ animationDelay: `${idx * 0.1}s` }"
              @click="selectAnimal(idx)">
              
              <!-- 普通动物卡片 -->
              <div v-if="!item.isCustomUpload" class="card-img" :style="{ backgroundImage: `url('${item.cover}')` }">
                <div class="card-overlay">
                  <div class="species-tag">{{ item.species }}</div>
                  <!-- 删除99%标签 -->
                </div>
              </div>
              
              <!-- 自定义上传卡片 -->
              <div v-else class="card-img custom-upload-area">
                <!-- 如果已上传图片 -->
                <div v-if="customUpload.image" class="uploaded-preview" 
                     :style="{ backgroundImage: `url('${customUpload.image.data}')` }">
                  <div class="card-overlay">
                    <div class="species-tag">{{ customUpload.mockData.species }}</div>
                    <!-- 删除99%标签 -->
                  </div>
                </div>
                
                <!-- 上传区域 -->
                <div v-else class="upload-placeholder">
                  <div v-if="customUpload.isUploading" class="upload-loading">
                    <div class="loading-spinner"></div>
                    <span>上传中...</span>
                  </div>
                  <div v-else class="upload-prompt">
                    <!-- 删除相机图标，改为简洁文字 -->
                    <span class="upload-text">点击上传</span>
                    <span class="upload-hint">支持 JPG/PNG</span>
                  </div>
                </div>
              </div>
              
              <div class="card-info">
                <div class="card-name">
                  {{ item.isCustomUpload && customUpload.image ? customUpload.mockData.name : item.name }}
                </div>
                <div class="card-desc">
                  {{ item.isCustomUpload && customUpload.image ? customUpload.mockData.desc : item.desc }}
                </div>
                <div class="card-stats">
                  <span class="stat-item">
                    <!-- 删除位置图标 -->
                    {{ item.isCustomUpload && customUpload.image ? customUpload.mockData.location : (item.location || '栖息地监测中') }}
                  </span>
                </div>
              </div>
              
              <div v-if="selectedAnimalIndex === idx && !uploadedImage" class="selected-indicator">
                <svg class="checkmark" viewBox="0 0 24 24" width="24" height="24">
                  <path d="M9 16.17L4.83 12L3.41 13.41L9 19L21 7L19.59 5.59L9 16.17Z" fill="white"/>
                </svg>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 多模态文件上传按钮组 -->
        <div class="upload-buttons-section">
          <h3 class="upload-buttons-title">📁 多模态文件上传（可选）</h3>
          <div class="upload-buttons-grid">
            <button 
              v-for="(config, fileType) in fileTypeConfigs" 
              :key="fileType"
              class="upload-type-btn"
              :class="{
                selected: uploadedFiles[fileType],
                uploading: uploadingStates[fileType]
              }"
              @click="triggerUploadByType(fileType)"
              :disabled="uploadingStates[fileType]"
            >
              <span class="btn-icon">{{ getFileTypeIcon(fileType) }}</span>
              <span class="btn-text">{{ getFileTypeName(fileType) }}</span>
              <span v-if="uploadedFiles[fileType]" class="btn-file-name">{{ uploadedFiles[fileType].name }}</span>
              <div v-if="uploadingStates[fileType]" class="btn-spinner"></div>
              <button 
                v-if="uploadedFiles[fileType] && !uploadingStates[fileType]" 
                class="btn-clear" 
                @click.stop="clearFile(fileType)"
                title="删除文件"
              >×</button>
            </button>
          </div>

          <!-- 隐藏的文件输入 -->
          <input ref="videoInput" type="file" accept="video/*" @change="e => onFileChange(e, 'video')" style="display:none;" />
          <input ref="sketchInput" type="file" accept="image/*" @change="e => onFileChange(e, 'sketch')" style="display:none;" />
          <input ref="rgbInput" type="file" accept="image/*" @change="e => onFileChange(e, 'rgb')" style="display:none;" />
          <input ref="infraredInput" type="file" accept="image/*" @change="e => onFileChange(e, 'infrared')" style="display:none;" />
          <input ref="audioInput" type="file" accept="audio/*" @change="e => onFileChange(e, 'audio')" style="display:none;" />
          <input ref="textInput" type="file" accept=".txt,.doc,.docx,.pdf" @change="e => onFileChange(e, 'text')" style="display:none;" />
        </div>

        <!-- 多模态文本输入 -->
        <div class="multimodal-input-section">
          <label for="multimodal-text" class="multimodal-label">
            <span class="label-icon">✍️</span>
            补充文本信息 (可选)
          </label>
          <textarea
            id="multimodal-text"
            v-model="additionalText"
            class="multimodal-textarea"
            placeholder="例如: 动物的行为、环境、发现时间等。详细的文本描述有助于提高REID准确率..."
          ></textarea>
        </div>
        
        <div class="action-section">
          <button class="start-btn" :disabled="!canStart || Object.values(uploadingStates).some(state => state)" @click="startAnalysis">
            <span v-if="!isStarting">🚀 启动分析引擎</span>
            <span v-else>
              <div class="btn-spinner"></div>
              启动中...
            </span>
          </button>
          
          <div class="tips-section">
            <div class="tip-item">
              <span class="tip-icon">💡</span>
              <span>我们的 <b>AdaFreq</b> 技术能有效忽略背景干扰，即使在杂乱环境中也能精准识别。</span>
            </div>
            <div class="tip-item">
              <span class="tip-icon">⚡</span>
              <span>得益于 <b>RotTrans</b> 模型，引擎能够抵抗无人机等设备的剧烈视角旋转，确保识别稳定性。</span>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 步骤2：识别动物-->
    <transition name="modal-fade">
      <div v-if="currentStep === 'analyzing'" class="modal-overlay">
        <div class="modal-content">
          <div class="modal-header">
            <h3>🚀 启动多维分析引擎...</h3>
            <div class="analysis-progress">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: analysisProgress + '%' }"></div>
              </div>
              <span class="progress-text">{{ Math.floor(analysisProgress) }}%</span>
            </div>
          </div>
          
          <div class="modal-img-wrap">
            <img :src="displayImage" class="modal-img" :class="{ 'rottrans-active': isRotTransActive }" />
            <div class="scan-line" :style="{ top: scanLineTop + 'px' }" v-if="currentStep === 'analyzing' && !isAdaFreqActive"></div>
            <div class="adafreq-heatmap" v-if="isAdaFreqActive"></div>
            <div class="detection-points">
              <div v-for="point in detectionPoints" :key="point.id" 
                class="detection-point" 
                :style="{ left: point.x + '%', top: point.y + '%', animationDelay: point.delay + 's' }">
              </div>
            </div>
          </div>
          
          <div class="modal-status">
            <div class="status-grid">
              <div v-for="(msg, idx) in statusMessages" :key="idx" 
                class="status-item" 
                :class="{ active: currentStatusIndex >= idx, completed: currentStatusIndex > idx }">
                <div class="status-icon">
                  <div v-if="currentStatusIndex > idx" class="icon-check">✓</div>
                  <div v-else-if="currentStatusIndex === idx" class="icon-loading"></div>
                  <div v-else class="icon-pending">•</div>
                </div>
                <span class="status-text">{{ msg }}</span>
              </div>
            </div>
            
            <div class="ai-insights">
              <div class="insight-item" v-for="insight in currentInsights" :key="insight">
                <span class="insight-icon">🧠</span>
                <span>{{ insight }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 步骤3：结果展示-->
    <transition name="slide-fade">
      <div v-if="currentStep === 'result'" class="result-wrap">
        <div class="result-header">
          <h2>🎉 成功生成动态数字生命档案</h2>
          <div class="result-summary">
            <div class="accuracy-badge">
              <span class="accuracy-label">REID识别准确率</span>
              <span class="accuracy-value">{{ resultAccuracy }}%</span>
            </div>
          </div>
        </div>

        <div class="result-content">
          <div class="info-card-light">
            <img :src="displayImage" class="info-img-light" />
            <div class="info-content-light">
              <div class="animal-header">
                <h3 class="animal-name">{{ resultAnimal.name }}</h3>
                <div class="protection-status" :class="getProtectionLevel(resultAnimal.species)">
                  {{ getProtectionText(resultAnimal.species) }}
                </div>
              </div>
              
              <div class="info-grid">
                <div class="info-row-light">
                  <span class="info-label">个体ID:</span>
                  <span class="info-value-light">{{ resultAnimal.id }}</span>
                </div>
                <div class="info-row-light">
                  <span class="info-label">物种:</span>
                  <span class="info-value-light">{{ resultAnimal.species }}</span>
                </div>
                <div class="info-row-light">
                  <span class="info-label">预估年龄:</span>
                  <span class="info-value-light">{{ resultAnimal.age }}</span>
                </div>
                <div class="info-row-light">
                  <span class="info-label">健康状况:</span>
                  <span class="health-pill" :class="getHealthClass(resultAnimal.health)">
                    {{ resultAnimal.health }}
                  </span>
                </div>
                <div class="info-row-light">
                  <span class="info-label">栖息地:</span>
                  <span class="info-value-light">{{ resultAnimal.habitat || '未知区域' }}</span>
                </div>
                <div class="info-row-light">
                  <span class="info-label">体重估算:</span>
                  <span class="info-value-light">{{ resultAnimal.weight || '65-85kg' }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="additional-info">
            <div class="map-card-light">
              <div class="map-title-light">
                <span>🗺️ 活动轨迹地图</span>
                <button class="view-details-btn">查看详情</button>
              </div>
              <div class="map-placeholder-light">
                <div class="map-content">
                  <div class="location-marker"></div>
                  <div class="activity-path"></div>
                  <span class="map-text">最近30天活动轨迹</span>
                </div>
              </div>
            </div>

            <div class="behavior-card">
              <div class="card-title">
                <span>📊 行为分析 (由 AMLP 驱动)</span>
              </div>
              <div class="behavior-stats">
                <div v-for="stat in behaviorStats" :key="stat.label" class="stat-box">
                  <span class="stat-label">{{ stat.label }}</span>
                  <div class="stat-bar">
                    <div class="stat-fill" :style="{ width: stat.value + '%', background: stat.color }"></div>
                  </div>
                  <span class="stat-value">{{ stat.value }}%</span>
                </div>
              </div>
            </div>
          </div>

          <!-- MLLM 分析报告 -->
          <div class="mllm-analysis-card">
            <div class="card-title">
              <span>🧠 MLLM 深度分析报告</span>
            </div>
            <div class="mllm-content">
              <div class="mllm-section">
                <h4 class="mllm-section-title">{{ mllmAnalysis.trajectory.title }}</h4>
                <p class="mllm-text" v-html="mllmAnalysis.trajectory.content"></p>
              </div>
              <div class="mllm-section">
                <h4 class="mllm-section-title">{{ mllmAnalysis.health.title }}</h4>
                <p class="mllm-text" v-html="mllmAnalysis.health.content"></p>
              </div>
              <div class="mllm-section">
                <h4 class="mllm-section-title">{{ mllmAnalysis.multimodal.title }}</h4>
                <p class="mllm-text" v-html="mllmAnalysis.multimodal.content"></p>
              </div>
            </div>
          </div>
        </div>

        <div class="action-buttons">
          <button class="back-btn secondary" @click="goBack">
            <span>🔄 重新REID识别</span>
          </button>
          <button class="export-btn primary" @click="exportResults">
            <span>📥 导出个体档案 (由 RSFD 生成)</span>
          </button>
          <button class="share-btn secondary" @click="shareResults">
            <span>📤 分享结果</span>
          </button>
        </div>

        <!-- 技术总结卡片 -->
        <div class="tech-summary-card">
          <div class="card-title">
            <span>⚙️ 本次分析调用技术模块</span>
          </div>
          <div class="tech-tags">
            <span class="tech-tag">AdaFreq</span>
            <span class="tech-tag">RotTrans</span>
            <span class="tech-tag">AMLP</span>
            <span class="tech-tag">RSFD</span>
          </div>
        </div>
      </div>
    </transition>

    <!-- 错误提示 -->
    <transition name="notification-fade">
      <div v-if="errorMessage" class="error-notification">
        <div class="error-content">
          <span class="error-icon">⚠️</span>
          <span>{{ errorMessage }}</span>
          <button @click="clearError" class="close-error">×</button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

// 响应式数据
const samples = ref([
  { 
    name: '东北虎-001', 
    desc: '左肩有独特斑纹', 
    species: '东北虎',
    text: '2023年4月拍摄于大兴安岭，晨间活动，精神状态良好。',
    cover: new URL('@/assets/Picture/AI识别/东北虎.jpg', import.meta.url).href, 
    id: 'CN-TGR-001', 
    age: '5岁', 
    health: '健康',
    habitat: '大兴安岭保护地',
    weight: '180-220kg',
    location: '中国东北'
  },
  { 
    name: '流浪犬-007', 
    desc: '左前腿有明显白色斑块', 
    species: '家犬',
    text: '城市边缘发现，疑似走失宠物，行为友善，需要救助。',
    cover: new URL('@/assets/Picture/AI识别/小狗.png', import.meta.url).href, 
    id: 'CN-DOG-007', 
    age: '2岁', 
    health: '轻微营养不良',
    habitat: '城市边缘地带',
    weight: '12-15kg',
    location: '城市郊区'
  },
  { 
    name: '大熊猫-088', 
    desc: '背部黑色条带较宽', 
    species: '大熊猫',
    text: '卧龙保护区竹林中进食，看起来很满足。',
    cover: new URL('@/assets/Picture/AI识别/大熊猫.jpg', import.meta.url).href, 
    id: 'CN-AML-088', 
    age: '8岁', 
    health: '需关注',
    habitat: '四川卧龙保护地',
    weight: '80-120kg',
    location: '四川'
  },
  // 添加自定义上传选项
  {
    name: '自定义上传',
    desc: '上传您的动物图片',
    species: '自定义',
    text: '点击上传您拍摄的动物图片进行AI识别',
    cover: '', // 将通过CSS显示上传图标
    id: 'CUSTOM-UPLOAD',
    age: '',
    health: '',
    habitat: '',
    weight: '',
    location: '',
    isCustomUpload: true // 标识这是自定义上传选项
  }
])

const currentStep = ref('select') // 'select' | 'analyzing' | 'result'
const selectedAnimalIndex = ref(null)
const additionalText = ref('')
const isStarting = ref(false)
const errorMessage = ref('')

// 多文件上传相关状态
const uploadedFiles = ref({
  video: null,
  sketch: null,
  rgb: null,
  infrared: null,
  audio: null,
  text: null
})

const uploadingStates = ref({
  video: false,
  sketch: false,
  rgb: false,
  infrared: false,
  audio: false,
  text: false
})

const dragStates = ref({
  video: false,
  sketch: false,
  rgb: false,
  infrared: false,
  audio: false,
  text: false
})

// refs for file inputs
const videoInput = ref(null)
const sketchInput = ref(null)
const rgbInput = ref(null)
const infraredInput = ref(null)
const audioInput = ref(null)
const textInput = ref(null)

// 文件类型配置
const fileTypeConfigs = {
  video: {
    accept: ['video/mp4', 'video/avi', 'video/mov', 'video/quicktime'],
    maxSize: 50 * 1024 * 1024, // 50MB
    errorSizeMsg: '视频文件大小不能超过50MB',
    errorTypeMsg: '仅支持MP4/AVI/MOV格式的视频文件'
  },
  sketch: {
    accept: ['image/jpeg', 'image/png', 'image/jpg', 'image/svg+xml'],
    maxSize: 10 * 1024 * 1024, // 10MB
    errorSizeMsg: '草图文件大小不能超过10MB',
    errorTypeMsg: '仅支持JPG/PNG/SVG格式的图片文件'
  },
  rgb: {
    accept: ['image/jpeg', 'image/png', 'image/jpg'],
    maxSize: 10 * 1024 * 1024, // 10MB
    errorSizeMsg: 'RGB图片文件大小不能超过10MB',
    errorTypeMsg: '仅支持JPG/PNG格式的图片文件'
  },
  infrared: {
    accept: ['image/jpeg', 'image/png', 'image/jpg'],
    maxSize: 10 * 1024 * 1024, // 10MB
    errorSizeMsg: '红外图片文件大小不能超过10MB',
    errorTypeMsg: '仅支持JPG/PNG格式的图片文件'
  },
  audio: {
    accept: ['audio/mp3', 'audio/wav', 'audio/m4a', 'audio/mpeg'],
    maxSize: 20 * 1024 * 1024, // 20MB
    errorSizeMsg: '音频文件大小不能超过20MB',
    errorTypeMsg: '仅支持MP3/WAV/M4A格式的音频文件'
  },
  text: {
    accept: ['text/plain', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/pdf'],
    maxSize: 5 * 1024 * 1024, // 5MB
    errorSizeMsg: '文本文件大小不能超过5MB',
    errorTypeMsg: '仅支持TXT/DOC/DOCX/PDF格式的文本文件'
  }
}

// 动画相关
const scanLineTop = ref(0)
const analysisProgress = ref(0)
const statusMessages = ref([
  "接收多模态输入，智能解析数据...",
  "深度特征提取，融合多源信息...",
  "RotTrans等核心算法提升识别鲁棒性...",
  "AI智能比对数据库个体...",
  "识别完成，生成数字档案与相似推荐..."
])
const currentStatusIndex = ref(0)
const resultAccuracy = ref(0)
const detectionPoints = ref([])
const currentInsights = ref([])
const isAdaFreqActive = ref(false)
const isRotTransActive = ref(false)

// 定时器
let scanTimer = null
let statusTimer = null
let progressTimer = null

// 计算属性
const resultAnimal = computed(() => {
  if (selectedAnimalIndex.value !== null) {
    const selected = samples.value[selectedAnimalIndex.value]
    // 如果是自定义上传且有上传的图片，返回模拟数据
    if (selected.isCustomUpload && customUpload.value.image) {
      return {
        ...customUpload.value.mockData,
        cover: customUpload.value.image.data
      }
    }
    return selected
  }
  return samples.value[0]
})

const canStart = computed(() => {
  // 可以选择动物样本，或者至少上传一个文件
  return selectedAnimalIndex.value !== null || hasUploadedFiles()
})

const displayImage = computed(() => {
  // 优先显示图片类型的文件
  if (uploadedFiles.value.rgb) return uploadedFiles.value.rgb.data
  if (uploadedFiles.value.sketch) return uploadedFiles.value.sketch.data
  if (uploadedFiles.value.infrared) return uploadedFiles.value.infrared.data
  
  // 如果选择了自定义上传且有图片
  if (selectedAnimalIndex.value !== null) {
    const selected = samples.value[selectedAnimalIndex.value]
    if (selected.isCustomUpload && customUpload.value.image) {
      return customUpload.value.image.data
    }
    return selected.cover
  }
  
  return ''
})

// 动态行为分析数据
const behaviorStats = computed(() => {
  const animal = resultAnimal.value
  const species = animal.species
  
  if (species === '家犬') {
    return [
      { label: '人类亲和度', value: 78, color: '#74b9ff' },
      { label: '觅食能力', value: 92, color: '#00b894' },
      { label: '警惕性', value: 65, color: '#fdcb6e' },
      { label: '健康指数', value: 68, color: '#e17055' }
    ]
  } else if (species === '东北虎') {
    return [
      { label: '活跃度', value: 85, color: '#00b894' },
      { label: '觅食频率', value: 72, color: '#fdcb6e' },
      { label: '社交互动', value: 60, color: '#74b9ff' }
    ]
  } else if (species === '大熊猫') {
    return [
      { label: '活跃度', value: 45, color: '#00b894' },
      { label: '进食频率', value: 88, color: '#fdcb6e' },
      { label: '休息质量', value: 92, color: '#74b9ff' }
    ]
  } else {
    // 默认数据
    return [
      { label: '活跃度', value: 75, color: '#00b894' },
      { label: '觅食频率', value: 68, color: '#fdcb6e' },
      { label: '社交互动', value: 55, color: '#74b9ff' }
    ]
  }
})

// 动态MLLM分析内容
const mllmAnalysis = computed(() => {
  const animal = resultAnimal.value
  const species = animal.species
  
  if (species === '家犬') {
    return {
      trajectory: {
        title: '活动轨迹与行为模式分析',
        content: `根据最近48小时的GPS追踪数据，个体"${animal.name}"主要活动在城市边缘的2平方公里范围内，表现出典型的流浪犬觅食行为模式。AI预测模型显示，该犬只有 <strong>85%</strong> 的概率会在傍晚时分返回「垃圾收集点B」附近觅食。`
      },
      health: {
        title: '健康状况与救助评估',
        content: `通过图像分析检测到轻微的营养不良症状，体重估算比标准值低约 <strong>20%</strong>。毛发状态显示缺乏定期护理，但未发现明显外伤或疾病征象。行为评估显示该犬对人类保持警惕但无攻击性。救助优先级：<span class="risk-level medium">中等</span>`
      },
      multimodal: {
        title: '多模态信息综合判断',
        content: `结合用户描述「${additionalText.value || '城市边缘发现，疑似走失宠物'}」与AI视觉分析（体态匀称、眼神清澈、无明显恐惧行为），判断该犬只可能为近期走失的家养宠物，建议优先进行身份识别和寻主工作。`
      }
    }
  } else if (species === '东北虎') {
    return {
      trajectory: {
        title: '轨迹分析与预测',
        content: `根据最近72小时的活动数据，个体"${animal.name}"的活动范围稳定在东北方向的15平方公里内，符合其物种习性。轨迹预测模型显示，未来24小时有 <strong>75%</strong> 的概率会向标记的「水源地A」移动。`
      },
      health: {
        title: '健康与行为风险评估',
        content: `视觉分析未发现明显外伤，毛色光泽度正常。但行为数据显示，其夜间活跃度相较于历史基线下降了 <strong>15%</strong>，可能为早期潜在疾病征兆或近期能量摄入不足。风险等级：<span class="risk-level low">低</span>`
      },
      multimodal: {
        title: '多模态信息交叉验证',
        content: `用户上传的文本描述「${additionalText.value || '无'}」与图像分析结果（步态稳健、精神状态良好）交叉验证一致。综合判断，该个体目前状态稳定。`
      }
    }
  } else {
    // 大熊猫或其他物种的默认分析
    return {
      trajectory: {
        title: '轨迹分析与预测',
        content: `根据最近72小时的活动数据，个体"${animal.name}"的活动范围符合其物种的典型行为模式。AI预测模型显示良好的栖息地适应性。`
      },
      health: {
        title: '健康与行为风险评估',
        content: `通过多模态分析，该个体整体健康状况良好，行为模式正常。建议持续监测以确保长期健康。风险等级：<span class="risk-level low">低</span>`
      },
      multimodal: {
        title: '多模态信息交叉验证',
        content: `综合图像、文本等多源信息，该个体识别准确度高，状态评估可靠。`
      }
    }
  }
})

// 辅助函数：检查是否有上传的文件
function hasUploadedFiles() {
  return Object.values(uploadedFiles.value).some(file => file !== null)
}

// 获取文件类型图标
function getFileTypeIcon(fileType) {
  const icons = {
    video: '🎥',
    sketch: '✏️',
    rgb: '🌈',
    infrared: '🔦',
    audio: '🔊',
    text: '📄'
  }
  return icons[fileType] || '📁'
}

// 获取文件类型名称
function getFileTypeName(fileType) {
  const names = {
    video: '视频',
    sketch: '草图',
    rgb: 'RGB图片',
    infrared: '红外图片',
    audio: '声音',
    text: '文本'
  }
  return names[fileType] || fileType
}

// 方法
function selectAnimal(idx) {
  const selected = samples.value[idx]
  
  // 如果点击的是自定义上传选项
  if (selected.isCustomUpload) {
    triggerCustomImageUpload()
    return
  }
  
  selectedAnimalIndex.value = idx
  // 清空所有上传的文件和自定义上传
  clearAllFiles()
  clearCustomUpload()
  additionalText.value = samples.value[idx].text || ''
}

function triggerUploadByType(fileType) {
  const inputRef = getInputRefByType(fileType)
  if (inputRef && inputRef.value) {
    inputRef.value.value = ''
    inputRef.value.click()
  }
}

function getInputRefByType(fileType) {
  const inputRefs = {
    video: videoInput,
    sketch: sketchInput,
    rgb: rgbInput,
    infrared: infraredInput,
    audio: audioInput,
    text: textInput
  }
  return inputRefs[fileType]
}

async function onFileChange(e, fileType) {
  const file = e.target.files[0]
  if (!file) return

  const config = fileTypeConfigs[fileType]
  if (!config) return

  // 验证文件大小
  if (file.size > config.maxSize) {
    showError(config.errorSizeMsg)
    return
  }

  // 验证文件类型
  if (!config.accept.includes(file.type)) {
    showError(config.errorTypeMsg)
    return
  }

  uploadingStates.value[fileType] = true
  
  try {
    if (['sketch', 'rgb', 'infrared'].includes(fileType)) {
      // 图片类型文件需要读取为Data URL
      const reader = new FileReader()
      reader.onload = (ev) => {
        uploadedFiles.value[fileType] = {
          name: file.name,
          size: file.size,
          type: file.type,
          data: ev.target.result
        }
        selectedAnimalIndex.value = null
        uploadingStates.value[fileType] = false
      }
      reader.onerror = () => {
        showError(`${fileType}文件读取失败，请重试`)
        uploadingStates.value[fileType] = false
      }
      reader.readAsDataURL(file)
    } else {
      // 其他类型文件只存储文件信息
      uploadedFiles.value[fileType] = {
        name: file.name,
        size: file.size,
        type: file.type,
        file: file
      }
      selectedAnimalIndex.value = null
      uploadingStates.value[fileType] = false
    }
  } catch (error) {
    showError(`${fileType}文件处理失败`)
    uploadingStates.value[fileType] = false
  }
}

function onDragOver(e, fileType) {
  dragStates.value[fileType] = true
}

function onDragLeave(e, fileType) {
  dragStates.value[fileType] = false
}

function onDrop(e, fileType) {
  dragStates.value[fileType] = false
  const files = e.dataTransfer.files
  if (files.length > 0) {
    const file = files[0]
    const event = { target: { files: [file] } }
    onFileChange(event, fileType)
  }
}

function clearFile(fileType) {
  uploadedFiles.value[fileType] = null
  uploadingStates.value[fileType] = false
  dragStates.value[fileType] = false
}

function clearAllFiles() {
  Object.keys(uploadedFiles.value).forEach(fileType => {
    clearFile(fileType)
  })
  clearCustomUpload()
}

function startAnalysis() {
  if (!canStart.value) return
  
  isStarting.value = true
  
  setTimeout(() => {
    currentStep.value = 'analyzing'
    isStarting.value = false
    initializeAnalysis()
  }, 800)
}

function initializeAnalysis() {
  scanLineTop.value = 0
  analysisProgress.value = 0
  currentStatusIndex.value = 0
  resultAccuracy.value = 0
  
  // 根据物种设置不同的状态消息
  const animal = resultAnimal.value
  const species = animal.species
  
  if (species === '家犬') {
    statusMessages.value = [
      "接收多模态输入，启动城市动物识别...",
      "分析犬类特征，检测品种与健康状况...",
      "匹配走失宠物数据库，评估救助优先级...",
      "生成个体身份档案与行为评估...",
      "识别完成，生成救助建议与寻主方案..."
    ]
  } else if (species === '东北虎') {
    statusMessages.value = [
      "接收多模态输入，智能解析数据...",
      "深度特征提取，融合多源信息...",
      "RotTrans等核心算法提升识别鲁棒性...",
      "AI智能比对数据库个体...",
      "识别完成，生成数字档案与相似推荐..."
    ]
  } else {
    // 默认状态消息
    statusMessages.value = [
      "接收多模态输入，智能解析数据...",
      "深度特征提取，融合多源信息...",
      "应用AI算法进行个体识别...",
      "智能比对数据库，确认个体身份...",
      "识别完成，生成数字档案..."
    ]
  }
  
  // 生成检测点
  detectionPoints.value = [
    { id: 1, x: 25, y: 30, delay: 0.5 },
    { id: 2, x: 75, y: 45, delay: 1.0 },
    { id: 3, x: 50, y: 70, delay: 1.5 },
    { id: 4, x: 30, y: 85, delay: 2.0 }
  ]

  // 扫描线动画
  scanTimer = setInterval(() => {
    scanLineTop.value += 3
    if (scanLineTop.value >= 148) scanLineTop.value = 0
  }, 40)

  // 真实的分阶段进度条动画
  startRealisticProgress()
  
  // 状态消息
  showNextStatus()
  
  // AI 洞察
  updateInsights()
}

// 新增：真实的分阶段进度动画
function startRealisticProgress() {
  // 定义每个阶段的进度范围和持续时间（模拟真实AI处理过程）
  const progressStages = [
    { start: 0, end: 15, duration: 800, speed: 'fast' },      // 快速启动
    { start: 15, end: 35, duration: 1200, speed: 'normal' },  // 数据加载
    { start: 35, end: 45, duration: 2000, speed: 'slow' },    // 特征提取（较慢）
    { start: 45, end: 75, duration: 1800, speed: 'variable' }, // 算法处理（变速）
    { start: 75, end: 90, duration: 1000, speed: 'normal' },  // 数据库比对
    { start: 90, end: 100, duration: 600, speed: 'fast' }     // 结果生成
  ]
  
  let currentStageIndex = 0
  
  function animateStage() {
    if (currentStageIndex >= progressStages.length) return
    
    const stage = progressStages[currentStageIndex]
    const startTime = Date.now()
    const startProgress = stage.start
    const endProgress = stage.end
    const duration = stage.duration
    
    function updateProgress() {
      const elapsed = Date.now() - startTime
      const progress = Math.min(elapsed / duration, 1)
      
      let easedProgress
      
      // 根据阶段特性应用不同的缓动函数
      switch (stage.speed) {
        case 'fast':
          // 快速线性
          easedProgress = progress
          break
        case 'slow':
          // 慢速，模拟复杂计算
          easedProgress = 1 - Math.cos(progress * Math.PI / 2)
          break
        case 'variable':
          // 变速，模拟算法处理的不确定性
          const noise = Math.sin(elapsed * 0.01) * 0.1
          easedProgress = progress + noise * (1 - progress)
          easedProgress = Math.max(0, Math.min(1, easedProgress))
          break
        default:
          // 正常缓动
          easedProgress = progress < 0.5 
            ? 2 * progress * progress 
            : 1 - Math.pow(-2 * progress + 2, 2) / 2
      }
      
      analysisProgress.value = startProgress + (endProgress - startProgress) * easedProgress
      
      if (progress < 1) {
        requestAnimationFrame(updateProgress)
      } else {
        currentStageIndex++
        // 在某些阶段之间添加短暂停顿，模拟真实处理
        if (currentStageIndex === 2 || currentStageIndex === 4) {
          setTimeout(() => animateStage(), 200)
        } else {
          animateStage()
        }
      }
    }
    
    updateProgress()
  }
  
  animateStage()
}

function showNextStatus() {
  const totalSteps = statusMessages.value.length;
  
  // 每个步骤的真实持续时间（不再是固定2.5秒）
  const stepDurations = [1000, 1500, 2200, 1800, 1200] // 毫秒
  
  if (currentStatusIndex.value < totalSteps - 1) {
    const currentDuration = stepDurations[currentStatusIndex.value] || 2000
    
    statusTimer = setTimeout(() => {
      currentStatusIndex.value++;
      updateInsights();
      
      // 触发特殊动画
      if (statusMessages.value[currentStatusIndex.value].includes('AdaFreq')) {
        isAdaFreqActive.value = true;
        setTimeout(() => { isAdaFreqActive.value = false; }, 2000);
      }
      if (statusMessages.value[currentStatusIndex.value].includes('RotTrans')) {
        isRotTransActive.value = true;
        setTimeout(() => { isRotTransActive.value = false; }, 2000);
      }
      
      showNextStatus(); // 递归调用
    }, currentDuration)
  } else {
    // 完成识别
    setTimeout(() => {
      analysisProgress.value = 100
      resultAccuracy.value = Math.floor(Math.random() * 5) + 95 // 95-99%
      
      // 保存识别记录到数据库
      saveIdentificationRecord()
      
      setTimeout(() => {
        currentStep.value = 'result'
        clearTimers()
      }, 1500)
    }, 1000)
  }
}

function updateInsights() {
  const animal = resultAnimal.value
  const species = animal.species
  
  let insights = []
  
  if (species === '家犬') {
    insights = [
      ['启动城市动物识别模块', '加载宠物特征数据库'],
      ['检测犬类品种特征', '分析毛色与体型特征'],
      ['评估营养状况与健康指标', '识别可能的身份标识'],
      ['匹配走失宠物数据库', '分析行为模式与社会化程度'],
      ['生成救助优先级评估', '构建个体身份档案'],
      ['整合多模态信息', '生成救助建议与寻主方案'],
      ['识别完成！建议立即启动救助流程']
    ]
  } else if (species === '东北虎') {
    insights = [
      ['加载多模态REID神经网络', '初始化红外图像预处理器'],
      ['应用跨场景识别模型', '检测个体基本轮廓'],
      ['分析动物身体高频纹理', '滤除背景及光照噪声'],
      ['校正拍摄角度偏差', '确保关键特征的稳定性'],
      ['并行分析健康状况、行为模式、年龄等', '构建多维度生物特征向量'],
      ['对比通用AI，生成结构化、专业化描述', '构建完整的个体数字档案'],
      ['REID匹配成功！已确认个体身份']
    ]
  } else {
    // 默认洞察（大熊猫等）
    insights = [
      ['加载物种识别模块', '初始化特征提取器'],
      ['分析个体特征', '检测关键识别点'],
      ['应用机器学习算法', '进行模式匹配'],
      ['评估识别置信度', '验证结果准确性'],
      ['生成个体档案', '整合分析结果'],
      ['完成多模态验证', '输出最终报告'],
      ['识别流程完成！']
    ]
  }
  
  if (currentStatusIndex.value < insights.length) {
    currentInsights.value = insights[currentStatusIndex.value]
  }
}

function getProtectionLevel(species) {
  const levels = {
    '东北虎': 'critical',
    '家犬': 'rescue', 
    '大熊猫': 'vulnerable'
  }
  return levels[species] || 'unknown'
}

function getProtectionText(species) {
  const texts = {
    '东北虎': '极危物种',
    '家犬': '需要救助',
    '大熊猫': '易危物种'
  }
  return texts[species] || '保护状态未知'
}

function getHealthClass(health) {
  const classes = {
    '健康': 'healthy',
    '亚健康': 'warning',
    '需关注': 'danger',
    '轻微营养不良': 'warning'
  }
  return classes[health] || 'unknown'
}

function exportResults() {
  // 模拟导出功能
  showError('导出功能开发中...', 'info')
}

function shareResults() {
  // 模拟分享功能
  if (navigator.share) {
    navigator.share({
      title: `AI识别结果: ${resultAnimal.value.name}`,
      text: `成功识别出${resultAnimal.value.species}，准确率${resultAccuracy.value}%`,
      url: window.location.href
    })
  } else {
    showError('分享功能开发中...', 'info')
  }
}

function goBack() {
  selectedAnimalIndex.value = null
  uploadedImage.value = null
  additionalText.value = ''
  currentStep.value = 'select'
  clearTimers()
}

function clearTimers() {
  if (scanTimer) clearInterval(scanTimer)
  if (statusTimer) clearTimeout(statusTimer)
  if (progressTimer) clearInterval(progressTimer)
}

function showError(message, type = 'error') {
  errorMessage.value = message
  setTimeout(() => {
    errorMessage.value = ''
  }, type === 'info' ? 2000 : 4000)
}

function clearError() {
  errorMessage.value = ''
}

// 保存识别记录到数据库
async function saveIdentificationRecord() {
  try {
    const animal = resultAnimal.value
    
    // 创建或更新动物信息
    const animalData = {
      speciesId: animal.id.split('-')[1] || 'UNKNOWN',
      speciesName: animal.species,
      animalId: animal.id,
      animalName: animal.name,
      age: animal.age,
      gender: '未知', // 可以根据需要扩展
      healthStatus: animal.health,
      isSick: animal.health === '需关注',
      isWarning: animal.health === '亚健康' || animal.health === '需关注',
      weight: animal.weight,
      habitat: animal.habitat,
      description: animal.desc || '通过AI-REID识别确认',
      firstDiscovered: new Date().toISOString(),
      lastSeen: new Date().toISOString()
    }
    
    // 保存动物信息
    await axios.post('/api/animals', animalData)
    
    // 保存轨迹记录（模拟当前位置）
    const trackingData = {
      species: animal.species,
      speciesId: animalData.speciesId,
      animalId: animal.id,
      timestamp: new Date().toISOString(),
      location: animal.location || animal.habitat || '识别地点',
      latitude: generateRandomLatitude(animal.location || animal.habitat),
      longitude: generateRandomLongitude(animal.location || animal.habitat)
    }
    
    // 保存轨迹数据
    await axios.post('/tracking', trackingData)
    
    console.log('识别记录已保存:', { animalData, trackingData })
    
  } catch (error) {
    console.error('保存识别记录失败:', error)
    // 静默处理错误，不影响用户体验
  }
}

// 根据地点生成模拟经纬度
function generateRandomLatitude(location) {
  const locationCoords = {
    '大兴安岭保护地': 48.2 + Math.random() * 0.5,
    '中国东北': 48.0 + Math.random() * 0.8,
    '城市郊区': 39.9 + Math.random() * 0.2,
    '城市边缘地带': 39.8 + Math.random() * 0.3,
    '四川卧龙保护地': 30.8 + Math.random() * 0.4,
    '四川': 30.5 + Math.random() * 1.0
  }
  
  return locationCoords[location] || (30.0 + Math.random() * 20.0)
}

function generateRandomLongitude(location) {
  const locationCoords = {
    '大兴安岭保护地': 127.0 + Math.random() * 2.0,
    '中国东北': 126.0 + Math.random() * 3.0,
    '城市郊区': 116.4 + Math.random() * 0.2,
    '城市边缘地带': 116.3 + Math.random() * 0.3,
    '四川卧龙保护地': 103.0 + Math.random() * 1.0,
    '四川': 102.0 + Math.random() * 3.0
  }
  
  return locationCoords[location] || (100.0 + Math.random() * 20.0)
}

// 生命周期
onMounted(() => {
  // 预加载图片
  samples.value.forEach(sample => {
    const img = new Image()
    img.src = sample.cover
  })
})

onUnmounted(() => {
  clearTimers()
})

// 在 <script setup> 中添加自定义上传相关状态
const customUpload = ref({
  image: null,
  isUploading: false,
  mockData: {
    name: '未知个体-???',
    desc: '待AI识别确认',
    species: '待识别',
    text: '通过自定义图片上传，等待AI分析...',
    id: 'CUSTOM-UNK-001',
    age: '未知',
    health: '待评估',
    habitat: '未知区域',
    weight: '待测量',
    location: '上传位置'
  }
})

// 添加自定义上传的文件输入引用
const customImageInput = ref(null)

// 新增：触发自定义图片上传
function triggerCustomImageUpload() {
  if (customImageInput.value) {
    customImageInput.value.value = ''
    customImageInput.value.click()
  }
}

// 新增：处理自定义图片上传
async function onCustomImageChange(e) {
  const file = e.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    showError('请选择图片文件')
    return
  }

  // 验证文件大小
  if (file.size > 10 * 1024 * 1024) {
    showError('图片文件大小不能超过10MB')
    return
  }

  customUpload.value.isUploading = true

  try {
    const reader = new FileReader()
    reader.onload = (ev) => {
      customUpload.value.image = {
        name: file.name,
        size: file.size,
        type: file.type,
        data: ev.target.result
      }
      
      // 选中自定义上传选项
      selectedAnimalIndex.value = samples.value.length - 1
      
      // 清空其他上传文件
      clearAllFiles()
      
      // 生成随机的模拟数据
      generateMockDataForCustomUpload(file.name)
      
      customUpload.value.isUploading = false
    }
    
    reader.onerror = () => {
      showError('图片读取失败，请重试')
      customUpload.value.isUploading = false
    }
    
    reader.readAsDataURL(file)
  } catch (error) {
    showError('图片处理失败')
    customUpload.value.isUploading = false
  }
}

// 新增：为自定义上传生成模拟数据
function generateMockDataForCustomUpload(fileName) {
  const randomSpecies = ['金丝猴', '藏羚羊', '华南虎', '白鱀豚', '扬子鳄', '朱鹮', '丹顶鹤', '家犬', '流浪猫']
  const randomLocations = ['云南', '西藏', '新疆', '内蒙古', '黑龙江', '四川', '青海', '城市郊区', '城市边缘地带']
  const randomHealth = ['健康', '亚健康', '需关注', '轻微营养不良']
  
  const species = randomSpecies[Math.floor(Math.random() * randomSpecies.length)]
  const location = randomLocations[Math.floor(Math.random() * randomLocations.length)]
  const health = randomHealth[Math.floor(Math.random() * randomHealth.length)]
  const confidence = Math.floor(Math.random() * 10) + 90 // 90-99%
  
  customUpload.value.mockData = {
    name: `${species}-${String(Math.floor(Math.random() * 999) + 1).padStart(3, '0')}`,
    desc: '通过AI识别的特征描述',
    species: species,
    text: `自定义上传图片：${fileName}，AI正在分析中...`,
    id: `CUSTOM-${species.substring(0, 3).toUpperCase()}-${String(Math.floor(Math.random() * 999) + 1).padStart(3, '0')}`,
    age: `${Math.floor(Math.random() * 15) + 1}岁`,
    health: health,
    habitat: `${location}保护区`,
    weight: `${Math.floor(Math.random() * 200) + 20}-${Math.floor(Math.random() * 300) + 100}kg`,
    location: location,
    confidence: confidence
  }
}

// 新增：清空自定义上传
function clearCustomUpload() {
  customUpload.value.image = null
  customUpload.value.isUploading = false
}
</script>

<style scoped>
/* 全局样式 */
.ai-root-light {
  width: 100vw;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  font-family: 'Inter', 'Noto Sans SC', Arial, sans-serif;
  position: relative;
  overflow-x: hidden;
  background: linear-gradient(180deg, #1F2937 0%, #111827 100%);
}

/* 背景图片 */
.ai-root-light::before {
  content: '';
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  width: 100vw;
  height: 100vh;
  background: url('/public/background.jpg') center center/cover no-repeat;
  opacity: 0.2;
  z-index: 0;
  pointer-events: none;
}

.ai-root-light > * {
  position: relative;
  z-index: 1;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin: 100px 0 20px 0;
  color: white;
  padding-top: 20px;
}

.main-title {
  font-size: 2.5rem;
  font-weight: 800;
  margin-bottom: 12px;
  background: linear-gradient(45deg, #58D68D, #2ECC71);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.main-subtitle {
  font-size: 1.1rem;
  font-weight: 400;
  opacity: 0.9;
  margin: 0;
  letter-spacing: 0.5px;
}

/* 进度指示器 */
.progress-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50px;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #6C757D;
  font-size: 0.9rem;
  font-weight: 500;
  transition: color 0.3s;
}

.step-item.active {
  color: #2ECC71;
}

.step-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #E9ECEF;
  color: #6C757D;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  transition: all 0.3s;
}

.step-item.active .step-circle {
  background: #2ECC71;
  color: white;
  transform: scale(1.1);
}

.step-line {
  width: 60px;
  height: 2px;
  background: #E9ECEF;
  margin: 0 20px;
}

/* 选择上传区域 */
.select-upload-wrap {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  min-height: calc(100vh - 200px);
}

.subtitle {
  font-size: 1.1rem;
  font-weight: 500;
  color: white;
  margin-bottom: 40px;
  letter-spacing: 0.5px;
  text-align: center;
  opacity: 0.95;
}

.card-center-flex {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.card-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 30px;
  max-width: 1200px;
  width: 100%;
  padding: 0 20px;
}

/* 卡片样式 */
.card-item {
  width: 100%;
  max-width: 280px;
  justify-self: center;
  height: 320px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 3px solid transparent;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.card-fade-in {
  opacity: 0;
  transform: translateY(30px);
  animation: fadeInUp 0.6s ease-out forwards;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-item:hover {
  transform: translateY(-12px) scale(1.02);
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}

.card-item.selected {
  border-color: #2ECC71;
  box-shadow: 0 20px 60px rgba(46, 204, 113, 0.3);
  transform: translateY(-8px) scale(1.02);
}

.card-img {
  height: 180px;
  width: 100%;
  background-size: cover;
  background-position: center;
  border-radius: 20px 20px 0 0;
  position: relative;
  overflow: hidden;
}

.card-overlay {
  position: absolute;
  top: 12px;
  left: 12px;
  right: 12px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.species-tag {
  background: rgba(0,0,0,0.7);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

.confidence-badge {
  background: rgba(46, 204, 113, 0.9);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
  backdrop-filter: blur(10px);
}

/* 上传卡片特殊样式 */
.upload-card .card-img.upload-img-bg {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2px dashed #2ECC71;
  box-sizing: border-box;
  padding: 10px;
  border-radius: 16px;
  height: 100%;
  transition: all 0.3s;
}

.upload-card .card-img.upload-img-bg.drag-over {
  border-color: #58D68D;
  background: linear-gradient(135deg, #58D68D 0%, #2ECC71 100%);
  transform: scale(1.02);
}

.upload-content, .uploading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-text {
  color: #2ECC71;
  font-size: 0.9rem;
  font-weight: 600;
}

.upload-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e9ecef;
  border-top: 3px solid #2ECC71;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.upload-preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 16px;
}

.card-info {
  background: white;
  padding: 20px 16px 16px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  justify-content: center;
  border-radius: 0 0 20px 20px;
}

.card-name {
  font-size: 1.2rem;
  font-weight: 700;
  color: #212529;
  margin-bottom: 6px;
  text-align: center;
}

.card-desc {
  font-size: 0.9rem;
  color: #6C757D;
  margin-bottom: 8px;
  text-align: center;
}

.card-stats {
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8rem;
  color: #6C757D;
}

.selected-indicator {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  background: #2ECC71;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: bounceIn 0.5s ease;
}

@keyframes bounceIn {
  0% { transform: scale(0); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

.clear-upload-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background: rgba(255,255,255,0.9);
  color: #dc3545;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}

.clear-upload-btn:hover {
  background: #dc3545;
  color: white;
  transform: scale(1.1);
}

/* 操作区域 */
.action-section {
  margin-top: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
}

.multimodal-input-section {
  width: 100%;
  max-width: 1200px;
  margin-top: 40px;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.multimodal-label {
  font-size: 1.1rem;
  font-weight: 500;
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
}

.multimodal-textarea {
  width: 100%;
  min-height: 100px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 12px 16px;
  color: white;
  font-size: 1rem;
  font-family: inherit;
  resize: vertical;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}

.multimodal-textarea::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.multimodal-textarea:focus {
  outline: none;
  border-color: #2ECC71;
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 0 3px rgba(46, 204, 113, 0.3);
}

/* 多模态文件上传按钮组样式 */
.upload-buttons-section {
  width: 100%;
  max-width: 1200px;
  margin: 40px auto 0;
  padding: 0 20px;
}

.upload-buttons-title {
  color: white;
  font-size: 1.2rem;
  font-weight: 600;
  text-align: center;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.upload-buttons-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.upload-type-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid transparent;
  border-radius: 12px;
  padding: 16px 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  position: relative;
  min-height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: white;
  font-family: inherit;
}

.upload-type-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(46, 204, 113, 0.5);
  transform: translateY(-2px);
}

.upload-type-btn.selected {
  border-color: #2ECC71;
  background: rgba(46, 204, 113, 0.2);
  box-shadow: 0 0 15px rgba(46, 204, 113, 0.4);
}

.upload-type-btn.uploading {
  border-color: #2ECC71;
  background: rgba(46, 204, 113, 0.1);
  cursor: not-allowed;
}

.upload-type-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 1.8rem;
  line-height: 1;
}

.btn-text {
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
}

.btn-file-name {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.8);
  text-align: center;
  word-break: break-all;
  max-width: 100%;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.2;
  margin-top: 4px;
}

.btn-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid #2ECC71;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-top: 4px;
}

.btn-clear {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 20px;
  height: 20px;
  background: rgba(220, 53, 69, 0.9);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  line-height: 1;
}

.btn-clear:hover {
  background: #dc3545;
  transform: scale(1.1);
}

@media (max-width: 768px) {
  .upload-buttons-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }
  
  .upload-type-btn {
    min-height: 70px;
    padding: 12px 8px;
    gap: 6px;
  }
  
  .btn-icon {
    font-size: 1.5rem;
  }
  
  .btn-text {
    font-size: 0.8rem;
  }
  
  .btn-file-name {
    font-size: 0.7rem;
  }
}

@media (max-width: 480px) {
  .upload-buttons-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .upload-type-btn {
    min-height: 65px;
    padding: 10px 6px;
  }
}

.start-btn {
  background: linear-gradient(135deg, #58D68D 0%, #2ECC71 100%);
  color: white;
  font-size: 1.2rem;
  font-weight: bold;
  border: none;
  border-radius: 50px;
  padding: 18px 48px;
  box-shadow: 0 8px 32px rgba(46, 204, 113, 0.4);
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 200px;
  justify-content: center;
}

.start-btn:disabled {
  background: #e9ecef;
  color: #6C757D;
  cursor: not-allowed;
  box-shadow: none;
}

.start-btn:hover:enabled {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(46, 204, 113, 0.5);
}

.btn-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.tips-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255,255,255,0.9);
  font-size: 0.9rem;
  padding: 8px 16px;
  background: rgba(255,255,255,0.1);
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

.tip-icon {
  font-size: 1rem;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  z-index: 9999;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(5px);
}

.modal-content {
  background: white;
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 20px 80px rgba(0,0,0,0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 400px;
  max-width: 500px;
  margin: 20px;
}

.modal-header {
  text-align: center;
  margin-bottom: 30px;
  width: 100%;
}

.modal-header h3 {
  font-size: 1.3rem;
  font-weight: 700;
  color: #212529;
  margin-bottom: 16px;
}

.analysis-progress {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #58D68D, #2ECC71);
  border-radius: 4px;
  transition: width 0.1s ease-out;
}

.progress-text {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2ECC71;
  min-width: 40px;
}

.modal-img-wrap {
  position: relative;
  width: 200px;
  height: 200px;
  margin-bottom: 30px;
}

.modal-img {
  width: 100%;
  height: 100%;
  border-radius: 20px;
  object-fit: cover;
  box-shadow: 0 8px 32px rgba(0,0,0,0.2);
  transition: transform 0.5s ease;
}

.modal-img.rottrans-active {
  animation: spin-back-forth 2s ease-in-out;
}

@keyframes spin-back-forth {
  0% { transform: rotate(0deg) scale(1); }
  25% { transform: rotate(15deg) scale(1.05); }
  50% { transform: rotate(-10deg) scale(1.05); }
  75% { transform: rotate(5deg) scale(1.05); }
  100% { transform: rotate(0deg) scale(1); }
}

.scan-line {
  position: absolute;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    rgba(46, 204, 113, 0.3) 20%, 
    #2ECC71 50%, 
    rgba(46, 204, 113, 0.3) 80%, 
    transparent 100%);
  border-radius: 2px;
  box-shadow: 0 0 20px #2ECC71;
  transition: top 0.05s linear;
}

.adafreq-heatmap {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  border-radius: 20px;
  background: radial-gradient(circle at 55% 50%, rgba(255, 0, 0, 0.7) 10%, rgba(255, 255, 0, 0.5) 35%, transparent 70%);
  mix-blend-mode: screen;
  pointer-events: none;
  animation: fadeInOut 2s ease-in-out;
}

@keyframes fadeInOut {
  0%, 100% { opacity: 0; }
  50% { opacity: 1; }
}

.detection-points {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.detection-point {
  position: absolute;
  width: 8px;
  height: 8px;
  background: #2ECC71;
  border-radius: 50%;
  animation: pulse 2s infinite;
  box-shadow: 0 0 12px #2ECC71;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.5); opacity: 0.7; }
}

.modal-status {
  width: 100%;
}

.status-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  transition: all 0.3s;
}

.status-item.active {
  color: #2ECC71;
}

.status-item.completed {
  color: #28a745;
}

.status-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.icon-loading {
  width: 16px;
  height: 16px;
  border: 2px solid #e9ecef;
  border-top: 2px solid #2ECC71;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.icon-pending {
  color: #6C757D;
}

.icon-check {
  color: #28a745;
}

.status-text {
  font-size: 1rem;
  font-weight: 500;
}

.ai-insights {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  border-left: 4px solid #2ECC71;
}

.insight-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 0.9rem;
  color: #495057;
}

.insight-item:last-child {
  margin-bottom: 0;
}

.insight-icon {
  font-size: 1rem;
}

/* 结果页面 */
.result-wrap {
  width: 100%;
  max-width: 1000px;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.result-header {
  text-align: center;
  color: white;
}

.result-header h2 {
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 16px;
  background: linear-gradient(45deg, #58D68D, #2ECC71);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.result-summary {
  display: flex;
  justify-content: center;
}

.accuracy-badge {
  background: rgba(255,255,255,0.95);
  border-radius: 30px;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  backdrop-filter: blur(10px);
}

.accuracy-label {
  font-size: 0.9rem;
  color: #6C757D;
}

.accuracy-value {
  font-size: 1.2rem;
  font-weight: bold;
  color: #2ECC71;
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-card-light {
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  padding: 32px;
  display: flex;
  gap: 32px;
  align-items: flex-start;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  backdrop-filter: blur(10px);
}

.info-img-light {
  width: 150px;
  height: 150px;
  border-radius: 20px;
  object-fit: cover;
  flex-shrink: 0;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}

.info-content-light {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.animal-header {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.animal-name {
  font-size: 1.5rem;
  font-weight: bold;
  color: #212529;
  margin: 0;
}

.protection-status {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
  text-transform: uppercase;
}

.protection-status.critical {
  background: #ffeaa7;
  color: #d63031;
}

.protection-status.vulnerable {
  background: #fab1a0;
  color: #e17055;
}

.protection-status.rescue {
  background: #74b9ff;
  color: #0984e3;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.info-row-light {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.95rem;
}

.info-label {
  color: #6C757D;
  font-weight: 500;
  min-width: 100px;
}

.info-value-light {
  color: #212529;
  font-weight: 600;
}

.health-pill {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
}

.health-pill.healthy {
  background: #d4edda;
  color: #155724;
}

.health-pill.warning {
  background: #fff3cd;
  color: #856404;
}

.health-pill.danger {
  background: #f8d7da;
  color: #721c24;
}

.additional-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.map-card-light, .behavior-card {
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  backdrop-filter: blur(10px);
}

.map-title-light, .card-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 1.1rem;
  font-weight: bold;
  color: #212529;
}

.view-details-btn {
  background: #eafaf1;
  color: #2ECC71;
  border: none;
  border-radius: 12px;
  padding: 6px 12px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.view-details-btn:hover {
  background: #2ECC71;
  color: white;
  transform: translateY(-1px);
}

.map-placeholder-light {
  height: 200px;
  background: #f8f9fa;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.map-content {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.location-marker {
  width: 12px;
  height: 12px;
  background: #2ECC71;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: pulse 2s infinite;
}

.activity-path {
  position: absolute;
  width: 60%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #2ECC71, transparent);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.map-text {
  color: #6C757D;
  font-size: 0.9rem;
  position: absolute;
  bottom: 12px;
  left: 50%;
  transform: translateX(-50%);
}

.behavior-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-label {
  font-size: 0.9rem;
  color: #6C757D;
  min-width: 80px;
}

.stat-bar {
  flex: 1;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.stat-fill {
  height: 100%;
  background: linear-gradient(90deg, #58D68D, #2ECC71);
  border-radius: 4px;
  transition: width 1s ease;
}

.stat-value {
  font-size: 0.9rem;
  font-weight: bold;
  color: #2ECC71;
  min-width: 40px;
  text-align: right;
}

.tech-summary-card {
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  backdrop-filter: blur(10px);
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
}

.tech-tag {
  background: #e6f7eb;
  color: #3e5247;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 20px;
}

.back-btn, .export-btn, .share-btn {
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 140px;
  justify-content: center;
}

.back-btn.secondary, .share-btn.secondary {
  background: rgba(255,255,255,0.95);
  color: #2ECC71;
  border: 2px solid #2ECC71;
}

.back-btn.secondary:hover, .share-btn.secondary:hover {
  background: #2ECC71;
  color: white;
  transform: translateY(-2px);
}

.export-btn.primary {
  background: linear-gradient(135deg, #58D68D 0%, #2ECC71 100%);
  color: white;
  border: none;
  box-shadow: 0 4px 16px rgba(46, 204, 113, 0.4);
}

.export-btn.primary span {
  font-size: 0.9rem;
  white-space: nowrap;
}

.export-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(46, 204, 113, 0.5);
}

/* 错误通知 */
.error-notification {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 10000;
  background: rgba(248, 215, 218, 0.95);
  border: 1px solid #f5c6cb;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.2);
  backdrop-filter: blur(10px);
  max-width: 400px;
}

.error-content {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #721c24;
}

.error-icon {
  font-size: 1.2rem;
}

.close-error {
  background: none;
  border: none;
  color: #721c24;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0;
  margin-left: auto;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.slide-fade-enter-active {
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}
.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(40px);
}

.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.4s;
}
.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}

.notification-fade-enter-active, .notification-fade-leave-active {
  transition: all 0.3s;
}
.notification-fade-enter-from, .notification-fade-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-title {
    font-size: 2rem;
  }
  
  .main-subtitle {
    font-size: 1rem;
  }
  
  .card-list {
    grid-template-columns: 1fr;
    gap: 20px;
    max-width: 300px;
  }
  
  .card-item {
    max-width: 100%;
  }
  
  .progress-indicator {
    padding: 16px;
    margin-bottom: 30px;
  }
  
  .step-item {
    font-size: 0.8rem;
  }
  
  .step-circle {
    width: 28px;
    height: 28px;
    font-size: 0.8rem;
  }
  
  .step-line {
    width: 40px;
    margin: 0 12px;
  }
  
  .modal-content {
    margin: 10px;
    padding: 30px 20px;
    min-width: auto;
    width: calc(100vw - 40px);
  }
  
  .info-card-light {
    flex-direction: column;
    text-align: center;
    padding: 24px;
  }
  
  .info-img-light {
    align-self: center;
  }
  
  .animal-header {
    justify-content: center;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .additional-info {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .back-btn, .export-btn, .share-btn {
    width: 200px;
  }
}

@media (max-width: 480px) {
  .page-header {
    margin: 20px 0 15px 0;
  }
  
  .main-title {
    font-size: 1.8rem;
  }
  
  .result-wrap {
    padding: 20px 10px;
  }
  
  .tips-section {
    text-align: center;
  }
  
  .tip-item {
    font-size: 0.8rem;
    padding: 6px 12px;
  }
}

.mllm-analysis-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  backdrop-filter: blur(10px);
  border-left: 4px solid #8B5CF6; /* MLLM 紫色 */
}

.mllm-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.mllm-section {
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}
.mllm-section:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.mllm-section-title {
  font-size: 1rem;
  font-weight: 600;
  color: #6D28D9;
  margin: 0 0 8px 0;
}

.mllm-text {
  font-size: 0.95rem;
  line-height: 1.7;
  color: #495057;
  margin: 0;
}

.mllm-text strong {
  color: #212529;
}

.risk-level {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: bold;
  color: white;
}
.risk-level.low { background-color: #28a745; }
.risk-level.medium { background-color: #ffc107; color: #212529; }
.risk-level.high { background-color: #dc3545; }

/* 步骤图标动画 */
.step-icon {
  display: inline-block;
  font-size: 16px;
  animation: pulse 1.5s infinite;
}

.step-icon.multimodal {
  animation: bounce 1s infinite;
}

.step-icon.feature {
  animation: sparkle 1.5s infinite;
}

.step-icon.rottrans {
  animation: rotate 2s infinite linear;
}

.step-icon.comparison {
  animation: blink 1s infinite;
}

.step-icon.result {
  animation: scale 1.2s infinite;
}

/* 步骤特定动画 */
.step-animations {
  margin-top: 15px;
  min-height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.multimodal-animation {
  display: flex;
  align-items: center;
  gap: 10px;
}

.input-icons {
  display: flex;
  gap: 5px;
}

.input-icon {
  display: inline-block;
  font-size: 20px;
  animation: slideInLeft 0.8s ease-out forwards;
  opacity: 0;
}

.flow-arrow {
  font-size: 18px;
  animation: pulse 1s infinite;
  color: #4CAF50;
}

.engine-icon {
  font-size: 24px;
  animation: glow 1.5s infinite alternate;
}

.feature-animation {
  position: relative;
  width: 100px;
  height: 30px;
}

.feature-points {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent 0%, #4CAF50 50%, transparent 100%);
  animation: scan 1.5s infinite;
}

.comparison-animation {
  display: flex;
  align-items: center;
  gap: 15px;
}

.database-icon, .ai-icon {
  font-size: 24px;
  animation: bounce 1s infinite;
}

.comparison-lines {
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, #4CAF50, #2196F3);
  animation: flow 1s infinite;
}

.result-animation {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-popup {
  font-size: 24px;
  animation: popIn 0.8s ease-out;
}

.similar-items {
  display: flex;
  gap: 5px;
}

.similar-item {
  font-size: 16px;
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0;
}

.similar-item:nth-child(1) { animation-delay: 0.2s; }
.similar-item:nth-child(2) { animation-delay: 0.4s; }
.similar-item:nth-child(3) { animation-delay: 0.6s; }

/* 动画关键帧 */
@keyframes slideInLeft {
  from {
    transform: translateX(-20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes glow {
  from {
    text-shadow: 0 0 5px #4CAF50;
  }
  to {
    text-shadow: 0 0 15px #4CAF50, 0 0 25px #4CAF50;
  }
}

@keyframes scan {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

@keyframes flow {
  0% { background-position: 0% 50%; }
  100% { background-position: 100% 50%; }
}

@keyframes popIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes fadeInUp {
  from {
    transform: translateY(10px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes sparkle {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; transform: scale(1.1); }
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0.3; }
}

@keyframes scale {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.15); }
}

/* 自定义上传卡片样式 */
.custom-upload-card {
  border: 2px dashed #ddd;
  transition: all 0.3s ease;
}

.custom-upload-card:hover {
  border-color: #4CAF50;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.2);
}

.custom-upload-card.uploading {
  border-color: #2196F3;
  background: linear-gradient(135deg, rgba(33, 150, 243, 0.05), rgba(33, 150, 243, 0.02));
}

.custom-upload-area {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fafafa, #f5f5f5);
  cursor: pointer;
}

.uploaded-preview {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  border-radius: 12px;
  position: relative;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
}

.upload-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #2196F3;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #e3f2fd;
  border-top: 3px solid #2196F3;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.upload-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.upload-text {
  font-weight: 600;
  font-size: 16px;
  color: #4CAF50;
}

.upload-hint {
  font-size: 12px;
  color: #999;
}

/* 删除图标相关样式 */
.card-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

/* 移除图标样式 */
.icon-location {
  display: none;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 自定义上传卡片选中状态 */
.custom-upload-card.selected {
  border-color: #4CAF50;
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.1), rgba(76, 175, 80, 0.05));
}
</style>