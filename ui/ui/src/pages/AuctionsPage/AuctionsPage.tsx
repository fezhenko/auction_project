import React, {useEffect, useState} from "react";
import {Auction} from "../../model/Auction";
import AuctionService from "../../service/AuctionService";
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";

const AuctionsPage = () => {
    const [auctions, setAuctions] = useState<Array<Auction>>([]);
    const [error, setError] = useState<string>("");

    useEffect(
        () => {
            AuctionService.getAuction()
                .then(response => setAuctions(response))
                .catch(error => setError(error.message));
        }, []
    );

    return (
        <div>
            {error}
            <TableContainer component={Paper}>
                <Table sx={{minWidth: 650}} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>Id</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>isPayed</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {auctions.map((auction) => (
                            <TableRow>
                                <TableCell component="th" scope="row">
                                    {auction.id}
                                </TableCell>
                                <TableCell component="th" scope="row">
                                    {auction.status}
                                </TableCell>
                                <TableCell component="th" scope="row">
                                    {auction.isPayed}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    );
}

export default AuctionsPage;