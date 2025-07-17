<template>
  <div class="dictionary-root">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="main-title">🔍 动物身份字典</h1>
      <p class="main-subtitle">基于AI-REID技术构建的全方位动物个体数字档案系统</p>
    </div>

    <!-- 统计面板 -->
    <div class="statistics-panel">
      <div class="stat-card">
        <div class="stat-icon">🐾</div>
        <div class="stat-content">
          <div class="stat-number">{{ totalAnimals }}</div>
          <div class="stat-label">个体总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🦁</div>
        <div class="stat-content">
          <div class="stat-number">{{ speciesCount }}</div>
          <div class="stat-label">物种数量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⚠️</div>
        <div class="stat-content">
          <div class="stat-number">{{ warningAnimals.length }}</div>
          <div class="stat-label">预警个体</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🏥</div>
        <div class="stat-content">
          <div class="stat-number">{{ sickAnimals.length }}</div>
          <div class="stat-label">患病个体</div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选工具栏 -->
    <div class="toolbar">
      <div class="search-section">
        <div class="search-box">
          <input
            v-model="searchQuery"
            placeholder="搜索个体名称、ID或物种..."
            class="search-input"
          />
          <button class="search-btn">🔍</button>
        </div>
      </div>
      
      <div class="filter-section">
        <select v-model="selectedSpecies" class="filter-select">
          <option value="">全部物种</option>
          <option v-for="species in availableSpecies" :key="species" :value="species">
            {{ species }}
          </option>
        </select>
        
        <select v-model="healthFilter" class="filter-select">
          <option value="">全部状态</option>
          <option value="健康">健康</option>
          <option value="亚健康">亚健康</option>
          <option value="需关注">需关注</option>
        </select>
        
        <select v-model="statusFilter" class="filter-select">
          <option value="">全部</option>
          <option value="warning">预警</option>
          <option value="sick">患病</option>
        </select>
        
        <button @click="clearFilters" class="clear-btn">清除筛选</button>
      </div>
    </div>

    <!-- 动物列表 -->
    <div class="animals-content">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="Object.keys(filteredAnimals).length === 0" class="empty-state">
        <div class="empty-icon">🔍</div>
        <h3>未找到匹配的个体</h3>
        <p>请尝试调整搜索条件或筛选器</p>
      </div>
      
      <div v-else class="species-groups">
        <div
          v-for="(animals, speciesName) in filteredAnimals"
          :key="speciesName"
          class="species-group"
        >
          <div class="species-header">
            <h2 class="species-title">
              <span class="species-emoji">{{ getSpeciesEmoji(speciesName) }}</span>
              {{ speciesName }}
              <span class="species-count">({{ animals.length }}个)</span>
            </h2>
          </div>
          
          <div class="animals-grid">
            <div
              v-for="animal in animals"
              :key="animal.animalId"
              class="animal-card"
              @click="openAnimalDetails(animal)"
            >
              <div class="animal-header">
                <div class="animal-id">{{ animal.animalId }}</div>
                <div class="status-indicators">
                  <span v-if="animal.isSick" class="status-badge sick">患病</span>
                  <span v-if="animal.isWarning" class="status-badge warning">预警</span>
                </div>
              </div>
              
              <div class="animal-info">
                <h3 class="animal-name">{{ animal.animalName }}</h3>
                <div class="animal-details">
                  <div class="detail-row">
                    <span class="detail-label">年龄:</span>
                    <span class="detail-value">{{ animal.age || '未知' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">性别:</span>
                    <span class="detail-value">{{ animal.gender || '未知' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">健康:</span>
                    <span class="detail-value health-status" :class="getHealthClass(animal.healthStatus)">
                      {{ animal.healthStatus || '未知' }}
                    </span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">栖息地:</span>
                    <span class="detail-value">{{ animal.habitat || '未知' }}</span>
                  </div>
                </div>
              </div>
              
              <div class="animal-footer">
                <div class="last-seen">
                  最后发现: {{ formatDate(animal.lastSeen) }}
                </div>
                <button class="view-details-btn">查看详情</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 动物详情模态框 -->
    <transition name="modal-fade">
      <div v-if="selectedAnimal" class="modal-overlay" @click="closeAnimalDetails">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h2>{{ selectedAnimal.animalName }} 详细档案</h2>
            <button @click="closeAnimalDetails" class="close-btn">×</button>
          </div>
          
          <div class="modal-body">
            <div class="animal-profile">
              <div class="profile-basic">
                <h3>基本信息</h3>
                <div class="profile-grid">
                  <div class="profile-item">
                    <span class="profile-label">个体ID:</span>
                    <span class="profile-value">{{ selectedAnimal.animalId }}</span>
                  </div>
                  <div class="profile-item">
                    <span class="profile-label">物种:</span>
                    <span class="profile-value">{{ selectedAnimal.speciesName }}</span>
                  </div>
                  <div class="profile-item">
                    <span class="profile-label">年龄:</span>
                    <span class="profile-value">{{ selectedAnimal.age || '未知' }}</span>
                  </div>
                  <div class="profile-item">
                    <span class="profile-label">性别:</span>
                    <span class="profile-value">{{ selectedAnimal.gender || '未知' }}</span>
                  </div>
                  <div class="profile-item">
                    <span class="profile-label">体重:</span>
                    <span class="profile-value">{{ selectedAnimal.weight || '未知' }}</span>
                  </div>
                  <div class="profile-item">
                    <span class="profile-label">健康状况:</span>
                    <span class="profile-value health-status" :class="getHealthClass(selectedAnimal.healthStatus)">
                      {{ selectedAnimal.healthStatus || '未知' }}
                    </span>
                  </div>
                </div>
              </div>
              
              <div class="profile-status">
                <h3>状态信息</h3>
                <div class="status-grid">
                  <div class="status-item">
                    <span class="status-label">患病状态:</span>
                    <span class="status-value" :class="{ 'status-yes': selectedAnimal.isSick }">
                      {{ selectedAnimal.isSick ? '是' : '否' }}
                    </span>
                  </div>
                  <div class="status-item">
                    <span class="status-label">预警状态:</span>
                    <span class="status-value" :class="{ 'status-yes': selectedAnimal.isWarning }">
                      {{ selectedAnimal.isWarning ? '是' : '否' }}
                    </span>
                  </div>
                </div>
              </div>
              
              <div class="profile-timeline">
                <h3>时间线</h3>
                <div class="timeline-grid">
                  <div class="timeline-item">
                    <span class="timeline-label">首次发现:</span>
                    <span class="timeline-value">{{ formatDate(selectedAnimal.firstDiscovered) }}</span>
                  </div>
                  <div class="timeline-item">
                    <span class="timeline-label">最后发现:</span>
                    <span class="timeline-value">{{ formatDate(selectedAnimal.lastSeen) }}</span>
                  </div>
                </div>
              </div>
              
              <div v-if="selectedAnimal.description" class="profile-description">
                <h3>个体描述</h3>
                <p>{{ selectedAnimal.description }}</p>
              </div>
            </div>
            
            <!-- 移动轨迹地图 -->
            <div class="tracking-map-section">
              <h3>移动轨迹地图 ({{ animalTrackings.length }} 个定位点)</h3>
              <div v-if="animalTrackings.length === 0" class="no-tracking">
                暂无轨迹数据
              </div>
              <div v-else class="map-container-modal">
                <div v-if="mapError" class="map-error">
                  {{ mapError }}
                </div>
                <div v-else class="map-wrapper">
                  <div ref="animalMapContainer" class="animal-map"></div>
                  
                  <!-- 地图控制按钮已移除，现在自动适应轨迹范围 -->
                  
                                <div class="map-legend">
                <div class="legend-item">
                  <img src="/pos.png" alt="定位点" class="legend-icon" />
                  <span>定位点</span>
                </div>
                <div class="legend-item">
                  <div class="legend-line"></div>
                  <span>移动路径</span>
                </div>
                <div class="legend-item legend-tip">
                  <span class="tip-icon">✋</span>
                  <span>拖拽移动地图</span>
                </div>
              </div>
                </div>
              </div>
            </div>
            
            <!-- 行动轨迹列表 -->
            <div class="tracking-section">
              <h3>行动轨迹记录 ({{ animalTrackings.length }} 条记录)</h3>
              <div v-if="animalTrackings.length === 0" class="no-tracking">
                暂无轨迹数据
              </div>
              <div v-else class="tracking-list">
                <div
                  v-for="(tracking, index) in animalTrackings.slice(0, 10)"
                  :key="tracking.id"
                  class="tracking-item"
                >
                  <div class="tracking-index">{{ index + 1 }}</div>
                  <div class="tracking-info">
                    <div class="tracking-location">{{ tracking.location }}</div>
                    <div class="tracking-coords">
                      经度: {{ tracking.longitude }}, 纬度: {{ tracking.latitude }}
                    </div>
                    <div class="tracking-time">{{ formatDateTime(tracking.timestamp) }}</div>
                  </div>
                </div>
                <div v-if="animalTrackings.length > 10" class="more-tracking">
                  还有 {{ animalTrackings.length - 10 }} 条记录...
                </div>
              </div>
            </div>
          </div>
          
          <div class="modal-footer">
            <button @click="closeAnimalDetails" class="modal-btn secondary">关闭</button>
            <button @click="exportAnimalData" class="modal-btn primary">导出档案</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import axios from 'axios'

// 响应式数据
const loading = ref(true)
const allAnimals = ref({})
const searchQuery = ref('')
const selectedSpecies = ref('')
const healthFilter = ref('')
const statusFilter = ref('')
const selectedAnimal = ref(null)
const animalTrackings = ref([])
const sickAnimals = ref([])
const warningAnimals = ref([])

// 地图相关
const animalMapContainer = ref(null)
const animalMap = ref(null)
const mapError = ref('')
const mapLoaded = ref(false)
let AMap = null

// 移除鼠标拖拽相关变量
// 移除地图控件相关变量
// 移除handleWheel函数

// 计算属性
const availableSpecies = computed(() => {
  return Object.keys(allAnimals.value).sort()
})

const totalAnimals = computed(() => {
  return Object.values(allAnimals.value).reduce((total, animals) => total + animals.length, 0)
})

const speciesCount = computed(() => {
  return Object.keys(allAnimals.value).length
})

const filteredAnimals = computed(() => {
  let filtered = { ...allAnimals.value }
  
  // 物种筛选
  if (selectedSpecies.value) {
    filtered = { [selectedSpecies.value]: filtered[selectedSpecies.value] || [] }
  }
  
  // 对每个物种的动物进行过滤
  Object.keys(filtered).forEach(species => {
    let animals = filtered[species]
    
    // 搜索过滤
    if (searchQuery.value) {
      const query = searchQuery.value.toLowerCase()
      animals = animals.filter(animal =>
        animal.animalName.toLowerCase().includes(query) ||
        animal.animalId.toLowerCase().includes(query) ||
        animal.speciesName.toLowerCase().includes(query)
      )
    }
    
    // 健康状态过滤
    if (healthFilter.value) {
      animals = animals.filter(animal => animal.healthStatus === healthFilter.value)
    }
    
    // 状态过滤
    if (statusFilter.value === 'warning') {
      animals = animals.filter(animal => animal.isWarning)
    } else if (statusFilter.value === 'sick') {
      animals = animals.filter(animal => animal.isSick)
    }
    
    if (animals.length === 0) {
      delete filtered[species]
    } else {
      filtered[species] = animals
    }
  })
  
  return filtered
})

// 方法
async function fetchAnimals() {
  try {
    loading.value = true
    
    // 并行请求所有数据
    const [animalsResponse, sickResponse, warningResponse] = await Promise.all([
      axios.get('/api/animals'),
      axios.get('/api/animals/sick'),
      axios.get('/api/animals/warning')
    ])
    
    allAnimals.value = animalsResponse.data.animals || {}
    sickAnimals.value = sickResponse.data || []
    warningAnimals.value = warningResponse.data || []
    
  } catch (error) {
    console.error('获取动物数据失败:', error)
    // 可以添加错误提示
  } finally {
    loading.value = false
  }
}

// 加载高德地图API
function loadAmapAPI() {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      AMap = window.AMap
      resolve()
      return
    }
    
    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${process.env.VUE_APP_AMAP_KEY || 'YOUR_AMAP_KEY_HERE'}`
    script.async = true
    
    script.onload = () => {
      if (window.AMap) {
        AMap = window.AMap
        resolve()
      } else {
        reject(new Error('高德地图API未正确加载'))
      }
    }
    
    script.onerror = () => {
      reject(new Error('加载高德地图API失败'))
    }
    
    document.head.appendChild(script)
  })
}

// 初始化动物轨迹地图
async function initAnimalMap() {
  if (!animalMapContainer.value || !AMap) {
    console.error('地图容器或AMap未准备好:', { 
      container: !!animalMapContainer.value, 
      AMap: !!AMap 
    })
    mapError.value = '地图环境未准备好'
    return
  }
  
  // 检查容器尺寸
  const containerRect = animalMapContainer.value.getBoundingClientRect()
  console.log('地图容器尺寸:', {
    width: containerRect.width,
    height: containerRect.height,
    display: window.getComputedStyle(animalMapContainer.value).display
  })
  
  if (containerRect.width === 0 || containerRect.height === 0) {
    console.error('地图容器尺寸为0，可能DOM未完全渲染')
    mapError.value = '地图容器尺寸异常'
    return
  }
  
  try {
    // 销毁现有地图
    if (animalMap.value) {
      animalMap.value.destroy()
      animalMap.value = null
    }
    
    console.log('开始创建地图，轨迹数据数量:', animalTrackings.value.length)
    
    // 计算轨迹的地理范围，确定最佳地图中心和缩放级别
    let mapCenter = [116.397428, 39.90923] // 默认中心（北京）
    let mapZoom = 10 // 默认缩放级别
    
    if (animalTrackings.value.length > 0) {
      const { center, zoom } = calculateOptimalMapView(animalTrackings.value)
      mapCenter = center
      mapZoom = zoom
      console.log('计算出的最佳地图视野:', { center: mapCenter, zoom: mapZoom })
    }
    
    // 创建地图实例 - 使用最简单的配置确保基本功能正常
    animalMap.value = new AMap.Map(animalMapContainer.value, {
      center: mapCenter,
      zoom: mapZoom,
      dragEnable: true,     // 明确启用拖拽
      keyboardEnable: false, // 禁用键盘操作避免冲突
      scrollWheel: false,    // 禁用滚轮缩放
      doubleClickZoom: true  // 允许双击缩放
    })
    
    mapLoaded.value = true
    console.log('地图创建成功')
    
    // 向地图容器添加自定义控制按钮
    addCustomMapControls()
    
    // 等待地图完全加载后再显示轨迹
    animalMap.value.on('complete', function() {
      console.log('地图加载完成事件触发')
      displayAnimalTrackingOnMap()
    })
    
  } catch (error) {
    console.error('初始化地图失败:', error)
    mapError.value = '地图初始化失败: ' + error.message
  }
}

// 添加自定义地图控制按钮
function addCustomMapControls() {
  try {
    // 创建自定义控制容器
    const controlContainer = document.createElement('div')
    controlContainer.className = 'custom-map-controls'
    controlContainer.style.cssText = `
      position: absolute;
      z-index: 10;
      top: 10px;
      right: 10px;
      background-color: rgba(0, 0, 0, 0.6);
      border-radius: 8px;
      padding: 5px;
      display: flex;
      flex-direction: column;
      gap: 5px;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
      border: 1px solid rgba(255, 255, 255, 0.2);
    `
    
    // 创建放大按钮
    const zoomInBtn = createMapButton('+', '放大地图')
    zoomInBtn.onclick = function(e) {
      e.stopPropagation() // 阻止事件冒泡
      if (animalMap.value) {
        const zoom = animalMap.value.getZoom()
        console.log('当前缩放级别:', zoom)
        animalMap.value.setZoom(zoom + 1)
        console.log('放大地图: 新缩放级别', zoom + 1)
      }
      return false
    }
    
    // 创建缩小按钮
    const zoomOutBtn = createMapButton('-', '缩小地图')
    zoomOutBtn.onclick = function(e) {
      e.stopPropagation() // 阻止事件冒泡
      if (animalMap.value) {
        const zoom = animalMap.value.getZoom()
        console.log('当前缩放级别:', zoom)
        if (zoom > 1) {
          animalMap.value.setZoom(zoom - 1)
          console.log('缩小地图: 新缩放级别', zoom - 1)
        }
      }
      return false
    }
    
    // 创建重置视图按钮
    const resetBtn = createMapButton('↺', '重置视图')
    resetBtn.onclick = function(e) {
      e.stopPropagation() // 阻止事件冒泡
      if (animalMap.value && animalTrackings.value.length > 0) {
        const { center, zoom } = calculateOptimalMapView(animalTrackings.value)
        animalMap.value.setZoomAndCenter(zoom, center)
        console.log('重置视图: 中心=', center, '缩放=', zoom)
      }
      return false
    }
    
    // 将按钮添加到控制容器
    controlContainer.appendChild(zoomInBtn)
    controlContainer.appendChild(zoomOutBtn)
    controlContainer.appendChild(resetBtn)
    
    // 将控制容器添加到地图容器
    animalMapContainer.value.appendChild(controlContainer)
    
    // 存储引用以便之后清理
    mapControlsContainer = controlContainer
    
    console.log('地图控制按钮添加成功')
  } catch (error) {
    console.error('添加地图控制按钮失败:', error)
  }
}

// 辅助函数: 创建地图按钮
function createMapButton(text, title) {
  const btn = document.createElement('button')
  btn.innerHTML = text
  btn.title = title
  btn.type = 'button'
  btn.style.cssText = `
    width: 36px;
    height: 36px;
    font-size: 18px;
    font-weight: bold;
    background-color: rgba(255, 255, 255, 0.2);
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background-color 0.2s;
    padding: 0;
    margin: 0;
  `
  
  // 添加悬停效果
  btn.onmouseover = function() {
    btn.style.backgroundColor = 'rgba(255, 255, 255, 0.3)'
  }
  btn.onmouseout = function() {
    btn.style.backgroundColor = 'rgba(255, 255, 255, 0.2)'
  }
  
  return btn
}

// 存储地图控制容器的引用
let mapControlsContainer = null

// 计算最佳地图视野
function calculateOptimalMapView(trackings) {
  const validCoords = []
  
  // 提取所有有效的坐标点
  trackings.forEach(tracking => {
    if (tracking.longitude && tracking.latitude) {
      try {
        const lng = typeof tracking.longitude === 'string' ? 
          parseFloat(tracking.longitude) : Number(tracking.longitude)
        const lat = typeof tracking.latitude === 'string' ? 
          parseFloat(tracking.latitude) : Number(tracking.latitude)
        
        if (!isNaN(lng) && !isNaN(lat)) {
          validCoords.push([lng, lat])
        }
      } catch (err) {
        console.warn('处理坐标时出错:', err)
      }
    }
  })
  
  if (validCoords.length === 0) {
    return {
      center: [116.397428, 39.90923],
      zoom: 10
    }
  }
  
  if (validCoords.length === 1) {
    return {
      center: validCoords[0],
      zoom: 15 // 单点时放大显示
    }
  }
  
  // 计算坐标范围
  let minLng = validCoords[0][0]
  let maxLng = validCoords[0][0]
  let minLat = validCoords[0][1]
  let maxLat = validCoords[0][1]
  
  validCoords.forEach(([lng, lat]) => {
    minLng = Math.min(minLng, lng)
    maxLng = Math.max(maxLng, lng)
    minLat = Math.min(minLat, lat)
    maxLat = Math.max(maxLat, lat)
  })
  
  // 计算中心点
  const centerLng = (minLng + maxLng) / 2
  const centerLat = (minLat + maxLat) / 2
  
  // 计算范围大小
  const lngRange = maxLng - minLng
  const latRange = maxLat - minLat
  const maxRange = Math.max(lngRange, latRange)
  
  // 根据范围大小确定合适的缩放级别
  let zoom = 15 // 默认缩放级别
  if (maxRange > 1) {
    zoom = 8  // 很大范围，省级视野
  } else if (maxRange > 0.5) {
    zoom = 9  // 大范围，市级视野
  } else if (maxRange > 0.1) {
    zoom = 11 // 中等范围，区级视野
  } else if (maxRange > 0.05) {
    zoom = 12 // 较小范围，街道级视野
  } else if (maxRange > 0.01) {
    zoom = 14 // 小范围，社区级视野
  } else {
    zoom = 16 // 很小范围，详细视野
  }
  
  console.log('轨迹范围分析:', {
    coordsCount: validCoords.length,
    lngRange: lngRange.toFixed(6),
    latRange: latRange.toFixed(6),
    maxRange: maxRange.toFixed(6),
    calculatedZoom: zoom
  })
  
  return {
    center: [centerLng, centerLat],
    zoom: zoom
  }
}

// calculateMapPadding函数已移除，现在在displayAnimalTrackingOnMap中直接计算边距

// fitTrackingBounds函数已移除，现在地图自动适应轨迹范围

// 在地图上显示动物轨迹
function displayAnimalTrackingOnMap() {
  console.log('开始显示轨迹，检查条件:', {
    map: !!animalMap.value,
    AMap: !!AMap,
    trackingsLength: animalTrackings.value.length
  })
  
  if (!animalMap.value || !AMap) {
    console.error('地图或AMap未准备好')
    return
  }
  
  if (!animalTrackings.value.length) {
    console.log('没有轨迹数据，显示空白地图')
    mapError.value = '该动物暂无轨迹记录'
    return
  }
  
  try {
    // 清除现有图层
    animalMap.value.clearMap()
    
    // 处理轨迹数据
    const validPoints = []
    const sortedTrackings = [...animalTrackings.value].sort((a, b) => 
      new Date(a.timestamp) - new Date(b.timestamp)
    )
    
    sortedTrackings.forEach(tracking => {
      if (tracking.longitude && tracking.latitude) {
        try {
          const lng = typeof tracking.longitude === 'string' ? 
            parseFloat(tracking.longitude) : Number(tracking.longitude)
          const lat = typeof tracking.latitude === 'string' ? 
            parseFloat(tracking.latitude) : Number(tracking.latitude)
          
          if (!isNaN(lng) && !isNaN(lat)) {
            validPoints.push({
              position: [lng, lat],
              timestamp: tracking.timestamp,
              location: tracking.location || '未知位置'
            })
          }
        } catch (err) {
          console.warn('处理坐标数据出错:', err)
        }
      }
    })
    
    console.log('处理后的有效轨迹点数量:', validPoints.length)
    
    if (validPoints.length === 0) {
      mapError.value = '没有有效的轨迹数据'
      console.error('没有有效的轨迹数据')
      return
    }
    
    // 确保拖拽功能已启用
    try {
      // 强制启用地图拖拽
      animalMap.value.setStatus({
        dragEnable: true,      // 启用鼠标拖拽
        keyboardEnable: false, // 禁用键盘平移以避免冲突
        doubleClickZoom: true, // 启用双击缩放
        zoomEnable: true       // 启用缩放功能
      })
      
      // 修改地图CSS确保指针样式正确
      if (animalMapContainer.value) {
        animalMapContainer.value.style.cursor = 'grab'
        
        // 在用户按下鼠标时更改鼠标样式
        animalMapContainer.value.addEventListener('mousedown', function() {
          this.style.cursor = 'grabbing'
        })
        
        // 在用户释放鼠标时还原鼠标样式
        animalMapContainer.value.addEventListener('mouseup', function() {
          this.style.cursor = 'grab'
        })
        
        // 在鼠标离开地图区域时还原鼠标样式
        animalMapContainer.value.addEventListener('mouseleave', function() {
          this.style.cursor = 'grab'
        })
      }
      
      console.log('地图拖拽功能已启用')
    } catch (error) {
      console.error('启用地图拖拽功能失败:', error)
    }
    
    // 先绘制轨迹线（按时间顺序连接），确保线条在标记点之下
    if (validPoints.length > 1) {
      const path = validPoints.map(p => p.position)
      
      console.log('绘制轨迹线，路径点数量:', path.length)
      
      const polyline = new AMap.Polyline({
        path: path,
        strokeColor: '#FF6B35', // 使用更明显的橙红色
        strokeWeight: 4, // 增加线条粗细
        strokeOpacity: 0.9, // 提高透明度
        strokeStyle: 'solid',
        lineJoin: 'round',
        lineCap: 'round',
        zIndex: 10 // 设置较低的层级，确保在标记点之下
      })
      
      animalMap.value.add(polyline)
      console.log('轨迹线已添加到地图')
    }
    
    // 再添加定位点标记，确保标记点在线条之上
    validPoints.forEach((point, index) => {
      const marker = new AMap.Marker({
        position: point.position,
        icon: new AMap.Icon({
          size: new AMap.Size(24, 24),
          image: '/pos.png',
          imageOffset: new AMap.Pixel(0, 0),
          imageSize: new AMap.Size(24, 24)
        }),
        offset: new AMap.Pixel(-12, -12),
        zIndex: 20 // 设置较高的层级，确保在线条之上
      })
      
      // 添加信息窗体
      const infoWindow = new AMap.InfoWindow({
        content: `<div class="info-window">
                   <h4>定位点 #${index + 1}</h4>
                   <p>时间: ${formatDateTime(point.timestamp)}</p>
                   <p>位置: ${point.location}</p>
                 </div>`,
        offset: new AMap.Pixel(0, -30)
      })
      
      marker.on('click', () => {
        infoWindow.open(animalMap.value, point.position)
      })
      
      animalMap.value.add(marker)
    })
    
    console.log('所有标记点已添加到地图，总数:', validPoints.length)
    
    // 延迟设置地图视野，确保所有图层都已完全渲染
    setTimeout(() => {
      try {
        // 设置地图视野以包含所有点
        const bounds = new AMap.Bounds()
        validPoints.forEach(point => {
          bounds.extend(point.position)
        })
        
        animalMap.value.setBounds(bounds, true, [40, 40, 40, 40])
        console.log('地图视野已设置为包含所有轨迹点')
      } catch (error) {
        console.error('设置地图视野失败:', error)
      }
    }, 300)
    
  } catch (error) {
    console.error('显示轨迹失败:', error)
    mapError.value = '显示轨迹失败: ' + error.message
  }
}

async function openAnimalDetails(animal) {
  try {
    selectedAnimal.value = animal
    mapError.value = ''
    
    console.log('开始获取动物详情:', animal.animalId)
    
    // 获取轨迹数据
    const response = await axios.get(`/api/animals/${animal.animalId}`)
    animalTrackings.value = response.data.trackings || []
    
    console.log('获取到轨迹数据:', animalTrackings.value.length, '条')
    
    // 等待DOM更新后初始化地图
    await nextTick()
    
    // 额外等待，确保模态框动画完成和DOM完全渲染
    setTimeout(async () => {
      try {
        // 确保地图API已加载
        if (!AMap) {
          console.log('正在加载高德地图API...')
          try {
            await loadAmapAPI()
            console.log('高德地图API加载成功')
          } catch (error) {
            console.error('加载地图API失败:', error)
            mapError.value = '地图API加载失败，请检查网络连接'
            return
          }
        }
        
        // 初始化地图
        await initAnimalMap()
      } catch (error) {
        console.error('延迟初始化地图失败:', error)
        mapError.value = '地图初始化失败'
      }
    }, 300) // 等待300ms确保模态框动画完成
    
  } catch (error) {
    console.error('获取动物详情失败:', error)
    animalTrackings.value = []
    mapError.value = '获取轨迹数据失败'
  }
}

function closeAnimalDetails() {
  selectedAnimal.value = null
  animalTrackings.value = []
  mapError.value = ''
  
  // 清理自定义控件
  removeCustomMapControls()
  
  // 销毁地图
  if (animalMap.value) {
    animalMap.value.destroy()
    animalMap.value = null
  }
}

// 清理自定义地图控件
function removeCustomMapControls() {
  try {
    if (mapControlsContainer && mapControlsContainer.parentNode) {
      mapControlsContainer.parentNode.removeChild(mapControlsContainer)
      mapControlsContainer = null
      console.log('已移除地图控制按钮')
    }
  } catch (error) {
    console.error('移除地图控制按钮失败:', error)
  }
}

function clearFilters() {
  searchQuery.value = ''
  selectedSpecies.value = ''
  healthFilter.value = ''
  statusFilter.value = ''
}

function getSpeciesEmoji(speciesName) {
  const emojiMap = {
    '东北虎': '🐅',
    '大熊猫': '🐼',
    '雪豹': '🐆',
    '华南虎': '🐯',
    '金丝猴': '🐒'
  }
  return emojiMap[speciesName] || '🦁'
}

function getHealthClass(healthStatus) {
  if (!healthStatus) return ''
  
  switch (healthStatus) {
    case '健康':
      return 'healthy'
    case '亚健康':
      return 'warning'
    case '需关注':
      return 'danger'
    default:
      return ''
  }
}

function formatDate(dateString) {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

function formatDateTime(dateString) {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleString('zh-CN')
}

function exportAnimalData() {
  // 实现导出功能
  console.log('导出动物档案:', selectedAnimal.value)
  alert('导出功能开发中...')
}

// 处理窗口大小变化
function handleResize() {
  if (animalMap.value) {
    // 延迟调整地图大小，确保容器尺寸已更新
    setTimeout(() => {
      animalMap.value.getSize()
      animalMap.value.resize()
      console.log('地图大小已调整')
    }, 100)
  }
}

// 生命周期
onMounted(async () => {
  await fetchAnimals()
  
  // 预加载地图API
  try {
    await loadAmapAPI()
  } catch (error) {
    console.warn('预加载地图API失败:', error)
  }
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

// 清理事件监听器
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  
  // 清理自定义控件
  removeCustomMapControls()
  
  // 销毁地图
  if (animalMap.value) {
    animalMap.value.destroy()
    animalMap.value = null
  }
})
</script>

<style scoped>
.dictionary-root {
  min-height: 100vh;
  background: linear-gradient(180deg, #1F2937 0%, #111827 100%);
  color: white;
  font-family: 'Inter', 'Noto Sans SC', Arial, sans-serif;
  position: relative;
  overflow-x: hidden;
}

/* 背景图片 */
.dictionary-root::before {
  content: '';
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: url('/public/background.jpg') center center/cover no-repeat;
  opacity: 0.1;
  z-index: 0;
  pointer-events: none;
}

.dictionary-root > * {
  position: relative;
  z-index: 1;
}

/* 页面标题 */
.page-header {
  text-align: center;
  padding: 60px 20px 30px;
}

.main-title {
  font-size: 2.5rem;
  font-weight: 800;
  margin-bottom: 12px;
  background: linear-gradient(45deg, #58D68D, #2ECC71);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.main-subtitle {
  font-size: 1.1rem;
  font-weight: 400;
  opacity: 0.9;
  margin: 0;
}

/* 统计面板 */
.statistics-panel {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  padding: 0 40px 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.stat-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.stat-icon {
  font-size: 2rem;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(46, 204, 113, 0.2);
  border-radius: 12px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 1.8rem;
  font-weight: bold;
  color: #2ECC71;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.8;
}

/* 工具栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 40px;
  max-width: 1200px;
  margin: 0 auto;
  gap: 20px;
  flex-wrap: wrap;
}

.search-section {
  flex: 1;
  min-width: 300px;
}

.search-box {
  display: flex;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  overflow: hidden;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  background: transparent;
  border: none;
  color: white;
  font-size: 1rem;
  outline: none;
}

.search-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.search-btn {
  padding: 12px 16px;
  background: #2ECC71;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.3s;
}

.search-btn:hover {
  background: #27AE60;
}

.filter-section {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  font-size: 0.9rem;
  backdrop-filter: blur(10px);
  min-width: 120px;
}

.filter-select option {
  background: #1F2937;
  color: white;
}

.clear-btn {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.clear-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* 内容区域 */
.animals-content {
  padding: 0 40px 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.loading-container {
  text-align: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid #2ECC71;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 1.5rem;
  margin-bottom: 12px;
  color: #2ECC71;
}

.empty-state p {
  opacity: 0.7;
}

/* 物种分组 */
.species-groups {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.species-group {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 20px;
  padding: 30px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.species-header {
  margin-bottom: 24px;
}

.species-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2ECC71;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
}

.species-emoji {
  font-size: 1.8rem;
}

.species-count {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 400;
}

/* 动物网格 */
.animals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.animal-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.animal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  background: rgba(255, 255, 255, 0.15);
}

.animal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.animal-id {
  font-size: 0.9rem;
  color: #2ECC71;
  font-weight: 600;
  background: rgba(46, 204, 113, 0.2);
  padding: 4px 8px;
  border-radius: 6px;
}

.status-indicators {
  display: flex;
  gap: 6px;
}

.status-badge {
  font-size: 0.75rem;
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: 600;
}

.status-badge.sick {
  background: rgba(220, 53, 69, 0.2);
  color: #dc3545;
}

.status-badge.warning {
  background: rgba(255, 193, 7, 0.2);
  color: #ffc107;
}

.animal-info {
  margin-bottom: 16px;
}

.animal-name {
  font-size: 1.3rem;
  font-weight: 700;
  margin-bottom: 12px;
  color: white;
}

.animal-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

.detail-label {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.detail-value {
  color: white;
  font-weight: 600;
}

.health-status.healthy {
  color: #28a745;
}

.health-status.warning {
  color: #ffc107;
}

.health-status.danger {
  color: #dc3545;
}

.animal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.last-seen {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.6);
}

.view-details-btn {
  background: #2ECC71;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.view-details-btn:hover {
  background: #27AE60;
  transform: translateY(-1px);
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  backdrop-filter: blur(5px);
}

.modal-content {
  background: #1F2937;
  border-radius: 20px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 80px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2ECC71;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.7);
  font-size: 1.5rem;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.modal-body {
  padding: 30px;
}

.animal-profile {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 30px;
}

.animal-profile h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2ECC71;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid rgba(46, 204, 113, 0.3);
}

.profile-grid,
.status-grid,
.timeline-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.profile-item,
.status-item,
.timeline-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.profile-label,
.status-label,
.timeline-label {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.profile-value,
.status-value,
.timeline-value {
  color: white;
  font-weight: 600;
}

.status-value.status-yes {
  color: #ffc107;
}

.profile-description p {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
  margin: 0;
}

.tracking-map-section {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 24px;
  margin-top: 30px;
}

.tracking-map-section h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2ECC71;
  margin: 0 0 16px 0;
}

.map-container-modal {
  position: relative;
  width: 100%;
  height: 500px; /* 增加地图容器高度以适应大跨度数据 */
  border-radius: 12px;
  overflow: hidden;
  background: #2F3E53; /* 地图容器背景 */
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.map-error {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(220, 53, 69, 0.8);
  color: white;
  padding: 15px 25px;
  border-radius: 10px;
  z-index: 10;
  font-size: 1rem;
  font-weight: 600;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.map-wrapper {
  width: 100%;
  height: 100%;
}

.animal-map {
  width: 100%;
  height: 100%;
}

/* 地图控制按钮样式已移除，现在使用自动适应轨迹范围 */

.map-legend {
  position: absolute;
  bottom: 10px;
  left: 10px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 10px;
  padding: 8px 12px;
  display: flex;
  align-items: center;
  gap: 15px;
  z-index: 10;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 0.8rem;
  opacity: 0.9;
}

.legend-marker {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  font-weight: 600;
  color: white;
}

.legend-marker.start {
  background: #2ECC71;
}

.legend-marker.end {
  background: #ffc107;
}

.legend-icon {
  width: 16px;
  height: 16px;
  object-fit: contain;
}

.legend-line {
  width: 20px;
  height: 3px;
  background: #FF6B35;
  border-radius: 1px;
}

.legend-tip {
  opacity: 0.8;
  font-size: 0.75rem;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding-top: 6px;
  margin-top: 6px;
}

.tip-icon {
  font-size: 0.9rem;
}

.tracking-point {
  width: 20px;
  height: 20px;
  background: #2ECC71;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 600;
  border: 2px solid white;
}

.info-window {
  padding: 10px;
  background: #1F2937;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  color: white;
  font-size: 0.9rem;
  line-height: 1.4;
}

.info-window h4 {
  margin-top: 0;
  margin-bottom: 8px;
  color: #2ECC71;
  font-size: 1rem;
}

.tracking-section {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 24px;
}

.tracking-section h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2ECC71;
  margin: 0 0 16px 0;
}

.no-tracking {
  color: rgba(255, 255, 255, 0.6);
  text-align: center;
  padding: 20px;
}

.tracking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 300px;
  overflow-y: auto;
}

.tracking-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
}

.tracking-index {
  width: 24px;
  height: 24px;
  background: #2ECC71;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 600;
  flex-shrink: 0;
}

.tracking-info {
  flex: 1;
}

.tracking-location {
  font-weight: 600;
  color: white;
  margin-bottom: 4px;
}

.tracking-coords {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 4px;
}

.tracking-time {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.5);
}

.more-tracking {
  text-align: center;
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9rem;
  padding: 8px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 30px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.modal-btn.secondary {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.modal-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.2);
}

.modal-btn.primary {
  background: #2ECC71;
  color: white;
}

.modal-btn.primary:hover {
  background: #27AE60;
  transform: translateY(-1px);
}

/* 动画 */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

/* 地图标记样式 */
.custom-marker {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 600;
  color: white;
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.start-marker {
  background: #2ECC71;
}

.end-marker {
  background: #ffc107;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-title {
    font-size: 2rem;
  }
  
  .statistics-panel {
    padding: 0 20px 20px;
    grid-template-columns: repeat(2, 1fr);
  }
  
  .toolbar {
    flex-direction: column;
    align-items: stretch;
    padding: 20px;
  }
  
  .search-section {
    min-width: auto;
  }
  
  .filter-section {
    justify-content: flex-start;
  }
  
  .animals-content {
    padding: 0 20px 40px;
  }
  
  .animals-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    margin: 10px;
    max-height: 95vh;
  }
  
  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 20px;
  }
  
  .profile-grid,
  .status-grid,
  .timeline-grid {
    grid-template-columns: 1fr;
  }

  .map-container-modal {
    height: 400px; /* 增加移动端地图容器高度以适应大跨度数据 */
  }

  /* 移动端地图控制按钮样式已移除 */

  .map-legend {
    flex-direction: column;
    align-items: flex-start;
    padding: 8px;
    gap: 8px;
  }

  .legend-item {
    flex-direction: column;
    align-items: center;
    gap: 4px;
  }
  
  .legend-tip {
    font-size: 0.7rem;
    padding-top: 4px;
    margin-top: 4px;
  }
  
  .tip-icon {
    font-size: 0.8rem;
  }

  .legend-marker {
    width: 12px;
    height: 12px;
    font-size: 0.6rem;
  }

  .legend-line {
    width: 15px;
    height: 1px;
  }

  .tracking-point {
    width: 15px;
    height: 15px;
    font-size: 0.7rem;
  }

  .info-window {
    font-size: 0.8rem;
  }
}
</style> 