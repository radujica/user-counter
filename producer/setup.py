from pathlib import Path
from typing import List

from setuptools import setup, find_packages


def read_requirements(path: str) -> List[str]:
    return list(Path(path).read_text().splitlines())


setup(
    name='producer',
    version='0.0.1',
    packages=find_packages(),
    install_requires=read_requirements('requirements.txt'),
    zip_safe=False,
    python_requires='>=3.6'
)
