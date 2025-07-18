import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { title: '野瞳万象-首页' }
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue'),
    meta: { title: '野瞳万象-关于我们' }
  },
  {
    path: '/technique',
    name: 'technique',
    component: () => import(/* webpackChunkName: "technique" */ '../views/Technique.vue'),
    meta: { title: '野瞳万象-核心技术' }
  },
  {
    path: '/project-overview',
    name: 'project-overview',
    component: () => import(/* webpackChunkName: "project-overview" */ '../views/ProjectOverview.vue'),
    meta: { title: '野瞳万象-项目概览' }
  },
  {
    path: '/ai-engine',
    name: 'ai-engine',
    component: () => import(/* webpackChunkName: "ai-engine" */ '../views/AIGuardianEngine.vue'),
    meta: { title: '野瞳万象-AI守护引擎' }
  },
  {
    path: '/map',
    name: 'ecology-data',
    component: () => import(/* webpackChunkName: "ecology-data" */ '../views/EcologyData.vue'),
    meta: { title: '野瞳万象-生态数据' }
  },
  {
    path: '/map-view',
    name: 'map',
    component: () => import(/* webpackChunkName: "map" */ '../views/Map.vue'),
    meta: { title: '野瞳万象-地图视图' }
  },
  {
    path: '/tech-demo',
    name: 'TechnologyDemo',
    component: () => import(/* webpackChunkName: "tech-demo" */ '../views/TechnologyDemo.vue'),
    meta: { title: '野瞳万象-技术展示' }
  },
  {
    path: '/animal-dictionary',
    name: 'animal-dictionary',
    component: () => import(/* webpackChunkName: "animal-dictionary" */ '../views/AnimalDictionary.vue'),
    meta: { title: '野瞳万象-动物词典' }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// Global navigation guard to set page title
router.beforeEach((to, from, next) => {
  // Set the page title
  document.title = to.meta.title || '野瞳万象';
  next();
})

export default router