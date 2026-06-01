import router from './router'
import {ElMessage} from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import {getToken} from '@/utils/auth'
import {isHttp, isPathMatch} from '@/utils/validate'
import {isRelogin} from '@/utils/request'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

NProgress.configure({showSpinner: false})

const whiteList = ['/login', '/register']
function resolvePath(basePath, routePath) {
    // 极其重要：防止 routePath 为 undefined 导致 .startsWith 报错卡死
    if (routePath == null) routePath = '';

    if (routePath.startsWith('http')) return routePath;
    if (routePath.startsWith('/')) return routePath;

    let finalBase = basePath || '/';
    if (finalBase.endsWith('/')) {
        return finalBase + routePath;
    } else {
        return finalBase + '/' + routePath;
    }
}

// 🚀 升级版：免疫死循环的 DFS 深度寻路算法
function getFirstValidRoute(routes, basePath = '') {
    for (let i = 0; i < routes.length; i++) {
        const route = routes[i];

        // 1. 跳过隐藏菜单和外链
        if (route.hidden || (route.path && route.path.startsWith('http'))) {
            continue;
        }

        // 2. 安全解析当前完整路径
        const fullPath = resolvePath(basePath, route.path);

        // 3. 判断是否有可见的子菜单
        const hasVisibleChildren = route.children && route.children.some(c => !c.hidden);

        if (hasVisibleChildren) {
            // 如果是目录（如 ParentView），递归往下钻
            const childPath = getFirstValidRoute(route.children, fullPath);
            if (childPath && childPath !== '/404') {
                return childPath; // 在子孙节点中找到了页面，向上返回
            }
        } else {
            // 4. 🚀【防卡死核心】：如果是叶子节点，必须检查它是不是旧首页！
            // 坚决不能返回 / 或 /index，否则会和 beforeEach 的拦截条件撞车，引发无限重启死循环！
            if (fullPath === '/' || fullPath === '/index') {
                continue; // 抛弃它，继续找下一个有效的业务菜单
            }

            // 找到了真正的安全叶子页面 (例如 /system/log/operlog)
            return fullPath;
        }
    }
    return '/404'; // 找遍了所有分支都没找到，才返回 404
}

const isWhiteList = (path) => {
    return whiteList.some(pattern => isPathMatch(pattern, path))
}

router.beforeEach((to, from, next) => {
    NProgress.start()
    if (getToken()) {
        to.meta.title && useSettingsStore().setTitle(to.meta.title)
        /* has token*/
        if (to.path === '/login') {
            next({path: '/'})
            NProgress.done()
        } else if (isWhiteList(to.path)) {
            next()
        } else {
            if (useUserStore().roles.length === 0) {
                isRelogin.show = true
                useUserStore().getInfo().then(() => {
                    isRelogin.show = false
                    usePermissionStore().generateRoutes().then(accessRoutes => {
                        accessRoutes.forEach(route => {
                            if (!isHttp(route.path)) {
                                router.addRoute(route)
                            }
                        })

                        // 🚀 【拦截点 1：刚登录获取完权限时】
                        if (to.path === '/' || to.path === '/index') {
                            const permissionStore = usePermissionStore()
                            console.log("当前角色的侧边栏菜单树：", permissionStore.sidebarRouters);
                            // 检查他到底有没有你的新首页权限
                            const hasCraftInfo = router.getRoutes().some(r => r.path === '/craft/info_craft')
                            // 有就去新首页，没有就去智能寻路找到的第一个菜单
                            let targetPath = hasCraftInfo ? '/craft/info_craft' : getFirstValidRoute(permissionStore.sidebarRouters)
                            targetPath = targetPath.replace(/\/\//g, '/')
                            next({path: targetPath, replace: true})
                            return
                        }

                        next({...to, replace: true})
                    })
                }).catch(err => {
                    useUserStore().logOut().then(() => {
                        ElMessage.error(err)
                        next({path: '/'})
                    })
                })
            } else {
                if (to.path === '/' || to.path === '/index') {
                    const permissionStore = usePermissionStore()
                    console.log("当前角色的侧边栏菜单树：", permissionStore.sidebarRouters);
                    const hasCraftInfo = router.getRoutes().some(r => r.path === '/craft/info_craft')
                    let targetPath = hasCraftInfo ? '/craft/info_craft' : getFirstValidRoute(permissionStore.sidebarRouters)
                    targetPath = targetPath.replace(/\/\//g, '/')
                    next({path: targetPath, replace: true})
                    return
                }
                next()
            }
        }
    } else {
        // 没有token
        if (isWhiteList(to.path)) {
            // 在免登录白名单，直接进入
            next()
        } else {
            next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
            NProgress.done()
        }
    }
})

router.afterEach(() => {
    NProgress.done()
})
