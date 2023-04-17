import {Credentials} from "../model/Credentials";
import {createUrl, isStoredJwt, post} from "../lib/http";
import {get} from "http";

class AuthService {
    async me() {

        return isStoredJwt()
            ? await get(createUrl("/api/me"))
            : null;
    };



}

