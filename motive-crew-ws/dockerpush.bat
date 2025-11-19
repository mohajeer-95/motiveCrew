echo @off
docker image build -t "cxm-ws-ashraf" .


docker tag cxm-ws-ashraf wirelessintegration/wireless:cxm-ws-ashraf


docker push wirelessintegration/wireless:cxm-ws-ashraf
