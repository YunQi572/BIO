package com.example.bio.service;

import com.example.bio.dto.TrajectoryAnalysisRequest;
import com.example.bio.dto.TrajectoryAnalysisResponse;
import com.example.bio.dto.TrajectoryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class TrajectoryAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(TrajectoryAnalysisService.class);

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TrajectoryAnalysisResponse analyzeTrajectory(TrajectoryAnalysisRequest request) {
        try {
            // 构建Gemini API请求
            Map<String, Object> requestBody = buildGeminiRequest(request);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建HTTP实体
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 调用Gemini API
            String response = restTemplate.postForObject(
                geminiApiUrl + "?key=" + geminiApiKey,
                entity,
                String.class
            );
            
            // 解析响应
            return parseGeminiResponse(response);
        } catch (Exception e) {
            // 异常处理
            TrajectoryAnalysisResponse errorResponse = new TrajectoryAnalysisResponse();
            errorResponse.setMigrationPattern("分析失败: " + e.getMessage());
            errorResponse.setHabitatPreference("无法获取栖息地偏好信息");
            errorResponse.setBehavioralTraits("无法获取行为特征信息");
            errorResponse.setConservationSuggestions("请稍后重试");
            return errorResponse;
        }
    }

    private Map<String, Object> buildGeminiRequest(TrajectoryAnalysisRequest request) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // 构建提示信息
        StringBuilder prompt = new StringBuilder();
        prompt.append("作为资深野生动物行为学和生态学专家，请基于以下动物迁徙轨迹数据进行深入的科学分析。\n\n");
        
        prompt.append("专业要求：\n");
        prompt.append("- 使用专业的动物行为学和生态学术语\n");
        prompt.append("- 每个分析部分需要详细阐述，包含具体的科学依据和推理过程\n");
        prompt.append("- 结合该物种的生物学特性和生态习性进行分析\n");
        prompt.append("- 提供量化的数据分析（如移动距离、速度、方向变化等）\n");
        prompt.append("- 不要使用数字编号，直接提供连贯的专业分析\n");
        prompt.append("- 语言要严谨、科学，体现动物学专家的专业水准\n\n");
        
        prompt.append("研究对象信息：\n");
        prompt.append("物种: ").append(request.getSpecies()).append("\n");
        prompt.append("个体标识: ").append(request.getAnimalId()).append("\n\n");
        
        prompt.append("观测数据：\n");
        List<TrajectoryPoint> points = request.getTrajectoryPoints();
        for (int i = 0; i < points.size(); i++) {
            TrajectoryPoint point = points.get(i);
            prompt.append("观测点").append(i + 1).append(" - 时间: ").append(point.getTimestamp())
                  .append(", 地理位置: ").append(point.getLocation())
                  .append(", 坐标: ").append(point.getLatitude()).append("°N, ").append(point.getLongitude()).append("°E")
                  .append("\n");
        }
        
        prompt.append("\n请按以下结构进行详细的科学分析：\n\n");
        
        prompt.append("迁徙模式分析：\n");
        prompt.append("请从以下维度进行深入分析：\n");
        prompt.append("- 空间移动模式：详细分析移动轨迹的几何特征、方向性和路径选择策略\n");
        prompt.append("- 时空动态特征：分析移动速度变化、停留时间分布和活动节律\n");
        prompt.append("- 移动行为类型：判断是否为觅食移动、领域巡查、迁徙行为或其他特定行为模式\n");
        prompt.append("- 环境适应性：分析移动模式与地形、植被、水源等环境因子的关系\n");
        prompt.append("请提供至少150字的详细分析。\n\n");
        
        prompt.append("栖息地偏好：\n");
        prompt.append("请从生态学角度深入分析：\n");
        prompt.append("- 栖息地类型识别：基于地理坐标和位置信息，分析该个体偏好的生境类型\n");
        prompt.append("- 微栖息地选择：分析在特定生境中的精细化空间利用模式\n");
        prompt.append("- 资源利用策略：分析对食物资源、水源、隐蔽场所等关键资源的利用模式\n");
        prompt.append("- 生境质量评估：从生态学角度评估所选择栖息地的质量和适宜性\n");
        prompt.append("请提供至少150字的专业分析。\n\n");
        
        prompt.append("行为特征：\n");
        prompt.append("请从动物行为学角度进行综合分析：\n");
        prompt.append("- 活动模式：分析日间/夜间活动规律、活动强度变化和行为节律\n");
        prompt.append("- 觅食行为：推断觅食策略、猎物搜寻模式和捕食行为特征\n");
        prompt.append("- 社会行为：分析领域行为、社会交往模式和个体间相互作用\n");
        prompt.append("- 适应性行为：分析对环境变化的行为响应和适应策略\n");
        prompt.append("- 生理生态特征：结合移动数据推断个体的生理状态和生态适应性\n");
        prompt.append("请提供至少150字的行为学分析。\n\n");
        
        prompt.append("保护建议：\n");
        prompt.append("请基于科学分析提出具体的保护策略：\n");
        prompt.append("- 栖息地保护：针对关键栖息地和生态廊道的具体保护措施\n");
        prompt.append("- 种群管理：基于个体行为特征的种群监测和管理建议\n");
        prompt.append("- 威胁因子控制：识别并提出控制主要威胁因子的具体措施\n");
        prompt.append("- 保护优先区域：确定需要优先保护的关键区域和生态节点\n");
        prompt.append("- 长期监测方案：设计科学的长期监测和评估体系\n");
        prompt.append("- 政策建议：提出相关的法律法规和政策建议\n");
        prompt.append("请提供至少150字的科学保护建议。\n\n");
        
        prompt.append("注意：每个分析部分都要体现专业的科学性和严谨性，使用准确的生态学和动物行为学术语，避免过于通俗化的表述。");
        
        // 构建Gemini API请求体
        Map<String, Object> content = new HashMap<>();
        content.put("role", "user");
        content.put("parts", List.of(Map.of("text", prompt.toString())));
        
        requestBody.put("contents", List.of(content));
        requestBody.put("generationConfig", Map.of(
            "temperature", 0.3,  // 降低温度以获得更稳定、专业的输出
            "topK", 40,
            "topP", 0.9,
            "maxOutputTokens", 4096  // 增加输出长度以支持更详细的分析
        ));
        
        return requestBody;
    }

    private TrajectoryAnalysisResponse parseGeminiResponse(String response) {
        try {
            // 解析Gemini API响应
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");
            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");
            
            // 解析文本响应
            return extractAnalysisFromText(text);
        } catch (Exception e) {
            throw new RuntimeException("解析Gemini API响应失败: " + e.getMessage(), e);
        }
    }

    private TrajectoryAnalysisResponse extractAnalysisFromText(String text) {
        TrajectoryAnalysisResponse response = new TrajectoryAnalysisResponse();
        
        // 添加调试日志
        logger.debug("AI返回的原始文本: {}", text);
        
        // 默认值
        response.setMigrationPattern("未能提取迁徙模式分析");
        response.setHabitatPreference("未能提取栖息地偏好");
        response.setBehavioralTraits("未能提取行为特征");
        response.setConservationSuggestions("未能提取保护建议");
        
        // 如果文本为空或null，直接返回默认值
        if (text == null || text.trim().isEmpty()) {
            logger.warn("AI返回的文本为空");
            return response;
        }
        
        // 清理文本：移除emoji和多余的空白字符
        String cleanedText = text.replaceAll("[🔄🏞️🦁🛡️]", "").trim();
        
        // 使用更智能的方式提取内容
        // 先找到所有的标题位置
        Map<String, Integer> titlePositions = new HashMap<>();
        
        // 查找各个标题的位置
        String[] titles = {"迁徙模式", "栖息地偏好", "行为特征", "保护建议"};
        for (String title : titles) {
            Pattern titlePattern = Pattern.compile("(?:" + title + ")(?:分析)?[:：]?", Pattern.CASE_INSENSITIVE);
            Matcher titleMatcher = titlePattern.matcher(cleanedText);
            if (titleMatcher.find()) {
                titlePositions.put(title, titleMatcher.end());
                logger.debug("找到标题 '{}' 位置: {}", title, titleMatcher.end());
            }
        }
        
        // 如果找到了标题，按位置提取内容
        if (!titlePositions.isEmpty()) {
            // 提取迁徙模式分析
            if (titlePositions.containsKey("迁徙模式")) {
                int start = titlePositions.get("迁徙模式");
                int end = cleanedText.length();
                
                // 找到下一个标题的位置作为结束点
                for (String nextTitle : new String[]{"栖息地偏好", "行为特征", "保护建议"}) {
                    if (titlePositions.containsKey(nextTitle) && titlePositions.get(nextTitle) > start) {
                        // 找到标题前的内容结束位置
                        String beforeTitle = cleanedText.substring(start, titlePositions.get(nextTitle));
                        int lastSentenceEnd = Math.max(
                            beforeTitle.lastIndexOf("。"),
                            Math.max(beforeTitle.lastIndexOf("！"), beforeTitle.lastIndexOf("？"))
                        );
                        if (lastSentenceEnd > 0) {
                            end = start + lastSentenceEnd + 1;
                        } else {
                            end = titlePositions.get(nextTitle);
                        }
                        break;
                    }
                }
                
                String content = cleanedText.substring(start, end).trim();
                // 移除可能包含的下一个标题
                content = content.replaceAll("(?:栖息地偏好|行为特征|保护建议)(?:分析)?[:：]?.*$", "").trim();
                if (!content.isEmpty()) {
                    response.setMigrationPattern(content);
                    logger.debug("提取迁徙模式分析: {}", content.substring(0, Math.min(content.length(), 100)) + "...");
                }
            }
            
            // 提取栖息地偏好
            if (titlePositions.containsKey("栖息地偏好")) {
                int start = titlePositions.get("栖息地偏好");
                int end = cleanedText.length();
                
                for (String nextTitle : new String[]{"行为特征", "保护建议"}) {
                    if (titlePositions.containsKey(nextTitle) && titlePositions.get(nextTitle) > start) {
                        String beforeTitle = cleanedText.substring(start, titlePositions.get(nextTitle));
                        int lastSentenceEnd = Math.max(
                            beforeTitle.lastIndexOf("。"),
                            Math.max(beforeTitle.lastIndexOf("！"), beforeTitle.lastIndexOf("？"))
                        );
                        if (lastSentenceEnd > 0) {
                            end = start + lastSentenceEnd + 1;
                        } else {
                            end = titlePositions.get(nextTitle);
                        }
                        break;
                    }
                }
                
                String content = cleanedText.substring(start, end).trim();
                content = content.replaceAll("(?:行为特征|保护建议)(?:分析)?[:：]?.*$", "").trim();
                if (!content.isEmpty()) {
                    response.setHabitatPreference(content);
                    logger.debug("提取栖息地偏好: {}", content.substring(0, Math.min(content.length(), 100)) + "...");
                }
            }
            
            // 提取行为特征
            if (titlePositions.containsKey("行为特征")) {
                int start = titlePositions.get("行为特征");
                int end = cleanedText.length();
                
                if (titlePositions.containsKey("保护建议") && titlePositions.get("保护建议") > start) {
                    String beforeTitle = cleanedText.substring(start, titlePositions.get("保护建议"));
                    int lastSentenceEnd = Math.max(
                        beforeTitle.lastIndexOf("。"),
                        Math.max(beforeTitle.lastIndexOf("！"), beforeTitle.lastIndexOf("？"))
                    );
                    if (lastSentenceEnd > 0) {
                        end = start + lastSentenceEnd + 1;
                    } else {
                        end = titlePositions.get("保护建议");
                    }
                }
                
                String content = cleanedText.substring(start, end).trim();
                content = content.replaceAll("(?:保护建议)(?:分析)?[:：]?.*$", "").trim();
                if (!content.isEmpty()) {
                    response.setBehavioralTraits(content);
                    logger.debug("提取行为特征: {}", content.substring(0, Math.min(content.length(), 100)) + "...");
                }
            }
            
            // 提取保护建议
            if (titlePositions.containsKey("保护建议")) {
                int start = titlePositions.get("保护建议");
                String content = cleanedText.substring(start).trim();
                // 移除可能的数字编号和其他干扰内容
                content = content.replaceAll("\\d+\\.\\s*问题本质.*$", "").trim();
                if (!content.isEmpty()) {
                    response.setConservationSuggestions(content);
                    logger.debug("提取保护建议: {}", content.substring(0, Math.min(content.length(), 100)) + "...");
                }
            }
        } else {
            // 如果没有找到标题，尝试其他方法
            logger.warn("未找到标准标题，尝试备用提取方法");
            
            // 按句号分割，尝试智能分配
            String[] sentences = cleanedText.split("。");
            if (sentences.length >= 4) {
                int quarter = sentences.length / 4;
                
                StringBuilder migration = new StringBuilder();
                for (int i = 0; i < quarter; i++) {
                    migration.append(sentences[i]).append("。");
                }
                response.setMigrationPattern(migration.toString().trim());
                
                StringBuilder habitat = new StringBuilder();
                for (int i = quarter; i < quarter * 2; i++) {
                    habitat.append(sentences[i]).append("。");
                }
                response.setHabitatPreference(habitat.toString().trim());
                
                StringBuilder behavior = new StringBuilder();
                for (int i = quarter * 2; i < quarter * 3; i++) {
                    behavior.append(sentences[i]).append("。");
                }
                response.setBehavioralTraits(behavior.toString().trim());
                
                StringBuilder conservation = new StringBuilder();
                for (int i = quarter * 3; i < sentences.length; i++) {
                    conservation.append(sentences[i]).append("。");
                }
                response.setConservationSuggestions(conservation.toString().trim());
                
                logger.debug("使用句子分割成功提取内容");
            } else {
                // 最后的备用方案
                response.setMigrationPattern(cleanedText.trim());
                response.setHabitatPreference("基于轨迹数据分析，该动物偏好特定的栖息地环境。");
                response.setBehavioralTraits("从移动模式可以观察到该动物的行为特征。");
                response.setConservationSuggestions("建议加强对该物种及其栖息地的保护。");
                logger.debug("使用整体文本作为分析结果");
            }
        }
        
        return response;
    }
}