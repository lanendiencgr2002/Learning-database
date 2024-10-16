javascript:(function() {
    const config = {
        sites: [
            {
                urlPattern: 'yuque.com/magestack/shortlink',
                textToFind: '请输入密码',
                password: 'xbkw'
            }
        ]
    };

    const matchingSite = config.sites.find(site => window.location.href.includes(site.urlPattern));

    if (matchingSite) {
        let targetElement = Array.from(document.querySelectorAll('*')).find(el => 
            el.textContent.includes(matchingSite.textToFind)
        );

        if (targetElement) {
            // 从目标元素开始向上查找，直到找到包含input的父元素
            let currentNode = targetElement;
            while (currentNode && !currentNode.querySelector('input')) {
                currentNode = currentNode.parentElement;
            }

            if (currentNode) {
                let inputElement = currentNode.querySelector('input');
                if (inputElement) {
                    inputElement.value = matchingSite.password;
                    inputElement.dispatchEvent(new Event('input', { bubbles: true }));
                    alert('密码已自动填充');
                } else {
                    alert('未找到input元素');
                }
            } else {
                alert('未找到包含input的父元素');
            }
        } else {
            alert('未找到包含指定文本的元素');
        }
    } else {
        alert('当前网页不匹配配置的网站');
    }
})();