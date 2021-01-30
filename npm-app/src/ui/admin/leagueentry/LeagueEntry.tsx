import React from 'react';
import styled from 'styled-components';
import ReactDOM from 'react-dom';
import {device} from "../../constants/breakpoints";

const Container = styled.div`
    display: flex;
    @media ${device.laptop} { 
        max-width: 800px;
    }

    @media ${device.desktop} {
        max-width: 1400px;
    }
`

const Content = styled.div`
    width: 66%
    background-color: #cccccc;
`

const SideBar = styled.div`
    width: 34%
    background-color: #ccccff;
`


const Link = styled.a`
  display: block;
  color: #006937;
  border-color: #006937;
  border-style: solid;
  border-width: 1px;
  cursor: pointer;
  font-weight: bold;
  text-align: center;
  padding: 5px;
  &:hover {
    background-color: #eeeeee;
  }
`;

const LeagueEntry = (url: string,
                         displayText: string) => {
    return (
        <Container>
            <Content>

            </Content>
            <SideBar>
                <h2>Add League Entry</h2>
                <form action="AdminLeagueEntries?method=add" method="post">
                    <label htmlFor="season">Season:</label>
                    <select name="season" id="season">
                        <option>2000/2001</option>
                    </select>
                    <label htmlFor="competition">Competition:</label>
                    <select name="competition" id="competition">
                        <option>Isthmian League</option>
                    </select>
                    <label htmlFor="club">Club:</label>
                    <select name="club" id="club">
                        <option>Hendon</option>
                    </select>
                    <input type="submit" value="Add"/>
                </form>

                <Link href={url}>{displayText}</Link>
            </SideBar>
        </Container>
    );
}

export const init = (element: Element,
                     url: string,
                     displayText: string) => {
    ReactDOM.render(LeagueEntry(url, displayText), element);
}