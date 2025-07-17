<template>
  <div class="trajectory-analysis">
    <div class="analysis-header">
      <h3>
        <span class="ai-icon">🧠</span> AI智能分析
      </h3>
      <div class="analysis-controls">
        <button @click="analyzeTrajectory" class="analyze-button" :disabled="isAnalyzing || !hasTrackingData">
          {{ isAnalyzing ? '分析中...' : '开始分析' }}
        </button>
      </div>
    </div>
    
    <div v-if="!hasTrackingData" class="no-data-message">
      请先选择动物轨迹数据进行分析
    </div>
    
    <div v-if="isAnalyzing" class="analyzing">
      <div class="loading-animation">
        <div class="dot"></div>
        <div class="dot"></div>
        <div class="dot"></div>
      </div>
      <p>AI正在分析数据，请稍候...</p>
    </div>
    
    <div v-if="analysisResult" class="analysis-result">
      <div class="result-section">
        <h4>🔄 迁移模式</h4>
        <div class="result-content" v-html="formatAnalysisText(analysisResult.migrationPattern)"></div>
      </div>
      
      <div class="result-section">
        <h4>🏞️ 栖息地偏好</h4>
        <div class="result-content" v-html="formatAnalysisText(analysisResult.habitatPreference)"></div>
      </div>
      
      <div class="result-section">
        <h4>🦁 行为特征</h4>
        <div class="result-content" v-html="formatAnalysisText(analysisResult.behavioralTraits)"></div>
      </div>
      
      <div class="result-section">
        <h4>🛡️ 保护建议</h4>
        <div class="result-content" v-html="formatAnalysisText(analysisResult.conservationSuggestions)"></div>
      </div>
    </div>
    
    <div v-if="errorMessage" class="error-message">
      <p>❌ {{ errorMessage }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import axios from 'axios';

const props = defineProps({
  trackingData: {
    type: Array,
    default: () => []
  },
  selectedSpecies: {
    type: String,
    default: ''
  },
  selectedAnimalId: {
    type: String,
    default: ''
  }
});

const isAnalyzing = ref(false);
const analysisResult = ref(null);
const errorMessage = ref('');

// 计算是否有轨迹数据可供分析
const hasTrackingData = computed(() => {
  return props.trackingData && props.trackingData.length >= 2;
});

// 监听轨迹数据变化，重置分析结果
watch(() => props.trackingData, () => {
  analysisResult.value = null;
  errorMessage.value = '';
});

// 准备轨迹数据用于API请求
const prepareTrajectoryData = () => {
  if (!hasTrackingData.value) return null;
  
  // 按时间戳排序
  const sortedData = [...props.trackingData].sort((a, b) => 
    new Date(a.timestamp) - new Date(b.timestamp)
  );
  
  // 提取关键信息并格式化日期
  return sortedData.map(point => {
    // 将日期格式从 "2023-04-01 15:30:00" 转换为 "2023-04-01T15:30:00"
    let formattedTimestamp = point.timestamp;
    if (typeof formattedTimestamp === 'string' && formattedTimestamp.includes(' ')) {
      formattedTimestamp = formattedTimestamp.replace(' ', 'T');
    }
    
    return {
      timestamp: formattedTimestamp,
      location: point.location,
      latitude: point.latitude,
      longitude: point.longitude
    };
  });
};

// 格式化分析文本，处理markdown风格的标记
const formatAnalysisText = (text) => {
  if (!text) return '';
  
  // 替换**文本**为加粗，使用更柔和的蓝色
  let formatted = text.replace(/\*\*(.*?)\*\*/g, '<strong style="color: #1976d2; font-weight: 600;">$1</strong>');
  
  // 替换*文本*为斜体，使用深蓝色
  formatted = formatted.replace(/\*(.*?)\*/g, '<em style="color: #1565c0; font-style: italic;">$1</em>');
  
  // 处理关键词高亮，使用淡蓝色背景
  const keywords = ['迁移', '栖息地', '行为', '保护', '觅食', '休息', '移动', '速度', '方向', '距离', '时间'];
  keywords.forEach(keyword => {
    const regex = new RegExp(`(${keyword})`, 'gi');
    formatted = formatted.replace(regex, '<span style="background: rgba(25, 118, 210, 0.1); padding: 2px 6px; border-radius: 4px; color: #1976d2; font-weight: 500;">$1</span>');
  });
  
  // 替换换行符
  formatted = formatted.replace(/\n/g, '<br>');
  
  // 清理多余的空行
  formatted = formatted.replace(/(<br>\s*){3,}/g, '<br><br>');
  
  return formatted;
};

// 分析轨迹数据
const analyzeTrajectory = async () => {
  if (!hasTrackingData.value) {
    errorMessage.value = '没有足够的轨迹数据进行分析';
    return;
  }
  
  isAnalyzing.value = true;
  errorMessage.value = '';
  analysisResult.value = null;
  
  try {
    const trajectoryData = prepareTrajectoryData();
    
    // 构建请求数据
    const requestData = {
      species: props.selectedSpecies,
      animalId: props.selectedAnimalId,
      trajectoryPoints: trajectoryData
    };
    
    // 调用Gemini API进行分析
    const response = await axios.post('/api/analysis/trajectory', requestData, {
      timeout: 30000 // 30秒超时
    });
    
    if (response.data) {
      analysisResult.value = response.data;
    } else {
      errorMessage.value = '分析结果为空';
    }
  } catch (error) {
    console.error('轨迹分析出错:', error);
    errorMessage.value = `分析失败: ${error.message || '未知错误'}`;
  } finally {
    isAnalyzing.value = false;
  }
};
</script>

<style scoped>
.trajectory-analysis {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 20px;
  border: 1px solid #e9ecef;
  color: #495057;
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e9ecef;
  flex-shrink: 0;
}

.analysis-header h3 {
  color: #495057;
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0;
  display: flex;
  align-items: center;
}

.ai-icon {
  margin-right: 8px;
  font-size: 1.3rem;
}

.result-section {
  margin-bottom: 16px;
  padding: 16px;
  background: #ffffff;
  border-radius: 8px;
  border-left: 4px solid #007bff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
}

.result-section h4 {
  color: #007bff;
  font-size: 1rem;
  margin-top: 0;
  margin-bottom: 12px;
  border-bottom: 1px solid #e9ecef;
  padding-bottom: 6px;
  display: flex;
  align-items: center;
}

.result-content {
  line-height: 1.6;
  color: #495057;
  min-height: 20px;
}

.analyze-button {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.analyze-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #0056b3 0%, #004085 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

.analyze-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.no-data-message {
  text-align: center;
  padding: 30px;
  color: #6c757d;
  font-style: italic;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px dashed #dee2e6;
}

.analyzing {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px;
  background: #f8f9fa;
  border-radius: 8px;
}

.loading-animation {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
}

.dot {
  width: 8px;
  height: 8px;
  background-color: #007bff;
  border-radius: 50%;
  animation: pulse 1.4s infinite ease-in-out;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes pulse {
  0%, 100% { transform: scale(0.8); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 1; }
}

.analysis-result {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.error-message {
  background: #f8d7da;
  border-left: 4px solid #dc3545;
  padding: 12px;
  border-radius: 6px;
  color: #721c24;
  margin-top: 12px;
}

/* 确保内容不会被截断 */
.result-content p {
  margin: 0;
  word-wrap: break-word;
}

.result-content strong {
  color: #007bff !important;
}

.result-content em {
  color: #6c757d !important;
}
</style>