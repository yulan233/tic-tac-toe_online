//路由配置
const routes = [
    {
        path: '/',
        component: () => import('../components/Home.vue')
    },
    {
        name:'usershow',
        path:'/usershow',
        component:()=>import('../components/userShow.vue')
    },
    {
        name:'game',
        path: '/game',
        component: () => import('../components/TicTacTonGame.vue')
    },
];

 
export default routes