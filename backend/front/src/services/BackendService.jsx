import axios from 'axios'

const API_URL = 'http://localhost:8080/api/v1'
const AUTH_URL = 'http://localhost:8080/auth'

class BackendService {

	login(login, password) {
		return axios.post(`${AUTH_URL}/login`, {login, password})
	}
	
	logout() {
		return axios.get(`${AUTH_URL}/logout`)
	}
}

export default new BackendService()
