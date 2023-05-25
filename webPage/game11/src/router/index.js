import {createRouter, createWebHistory,createWebHashHistory} from 'vue-router'
import routes from './routes'
 
// 创建路由
const router = createRouter({
    history: createWebHashHistory(), 
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    //不放行game
    if(to.path === '/game'){
        // token
        const token = sessionStorage.getItem('token')
        if(token==null){
            next('/')
        }else{
            next()   
        }
    }else{
        next()
    }
    // next()
})

// 导出路由
export default router