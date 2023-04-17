import axios from "axios";
import {Auction} from "../model/Auction";



class AuctionService {

    async getAuction() : Promise<Array<Auction>> {
        const response = await axios.get<Array<Auction>>(`http://localhost:8081/api/v1/auctions`);
        return response.data;
    }
}

export default new AuctionService();