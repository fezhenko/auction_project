import React, {useState} from "react";
import {Auction} from "../../model/Auction";
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";

const AuctionPage = () => {
    const [auction, setAuction] = useState<Auction>();
    const [error, setError] = useState<string>("");

    // useEffect(
    //     () => {
    //         AuctionService.getAuction()
    //             .then(response => setAuction(response))
    //             .catch(error => setError(error.message));
    //     }, []
    // );

    return (
        <div>
            {error}

            <TableContainer component={Paper}>
                <Table sx={{minWidth: 650}} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>Dessert (100g serving)</TableCell>
                            <TableCell align="right">Calories</TableCell>
                            <TableCell align="right">Fat&nbsp;(g)</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {
                            <TableRow
                                key={auction?.id}
                                sx={{'&:last-child td, &:last-child th': {border: 0}}}
                            >
                                <TableCell component="th" scope="row">
                                    {auction?.status}
                                </TableCell>
                                <TableCell align="right">{auction?.isPayed}</TableCell>
                            </TableRow>
                        }
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    );
}

export default AuctionPage;