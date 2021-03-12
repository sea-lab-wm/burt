import Cookies from 'universal-cookie';

class SessionManager {

    static cookies = new Cookies();

    static getSessionId() {
        return this.cookies.get("sessionId");
    }

    static noSession(){
        return this.getSessionId() === undefined;
    }

    static setSessionId(sessionId) {
        if(sessionId ===undefined) throw "The session id cannot be null"
        this.cookies.set('sessionId', sessionId, {path: '/', secure: false, sameSite: "lax"});
    }

    static endSession(){
        this.cookies.remove('sessionId')
    }
}

export default SessionManager;