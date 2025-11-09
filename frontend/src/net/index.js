import axios from 'axios'
import {ElMessage} from "element-plus";

const authItemName = 'access_token'

const defaultFailure = (message, code, url) => {
    console.warn(`Request to ${url} failed ${code}: ${message}`)
    ElMessage.warning('Something went wrong: ' + message)
}

const defaultError = (err) => {
    console.error(err)
    ElMessage.error('Something went wrong, please contact the administrator.')
}

function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    if (!str) return null
    const authObject = JSON.parse(str)
    if (authObject.expire < Date.now()) {
        deleteAccessToken()
        ElMessage.warning('Your session has expired, please log in again.')
        return null
    }
    return authObject.token
}

function storeAccessToken(token, remember, expire) {
    const authObject = {
        token: token,
        expire: expire
    }
    const authString = JSON.stringify(authObject)
    if (remember) {
        localStorage.setItem(authItemName, authString)
    } else {
        sessionStorage.setItem(authItemName, authString)
        localStorage.removeItem(authItemName)
    }
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

function accessHeader() {
    const token = takeAccessToken()
    return token ? {
        'Authorization': 'Bearer ' + token
    } : {}
}

function internalPost(url, data, header, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, { headers: header }).then(({ data }) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}

function internalGet(url, header, success, failure = defaultFailure, error = defaultError) {
    axios.get(url, { headers: header }).then(({ data }) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}

function get(url, success, failure = defaultFailure, error = defaultError) {
    internalGet(url, accessHeader(), success, failure, error)
}

function post(url, data, success, failure = defaultFailure, error = defaultError) {
    internalPost(url, data, accessHeader(), success, failure, error)
}

function login(username, password, remember, success) {
    internalPost(
        '/api/auth/login',
        {
            username: username,
            password: password
        }, {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        (data) => {
            storeAccessToken(data.token, remember, data.expire)
            ElMessage.success('Login successfully! Welcome back, ' + data.username)
            success(data)
        }
    )
}

function logout(success, failure = defaultFailure) {
    get(
        '/api/auth/logout',
        () => {
            deleteAccessToken()
            ElMessage.success('Logout successfully!')
            success()
        },
        failure
    )
}

export {get, post, login, logout}